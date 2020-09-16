package pers.li.redis.high;

import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * create by lishengbo on 2018-05-11 09:06
 * 哨兵机制具体实现:
 *  ·选举机制：轮询
 *  ·实现：
 *      1.启动定时：N秒执行一次
 *          ·检查master是否可用（ping通）
 *            ·可用：
 *              1.更新slave列表
 *              2.检查bad server列表
 *                  ·循坏ping，通则正常，不通则跳出
 *                  ·正常后挂载到当前master
 *                  ·更新slave列表
 *
 *            ·不可用：
 *              1.将当前redis实例划入bad server
 *              2.从slave中选举一个座位master
 *              3.现有的slave切换到新的master
 *              4.继续走可用流程
 */
public class SentinelConfigServer {

    static String master;
    //所有slave
    static final Vector<String> slaveRedisServers=new Vector<String>();
    //坏掉的实例
    static final Vector<String> badRedisServers=new Vector<String>();

    public static void main(String[] args) throws IOException {
        //配置redis-master
        master="127.0.0.1:6381";
        //定时任务
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                //核心三步：官方的哨兵机制核心也用此实现
                //检查master
                checkMaster();
                //更新slave列表
                updateSlave();
                //检查坏掉的服务有没有恢复
                checkBadserver();
            }




        },3000L,3000L);

        //最后开启端口接收请求
        open();


    }

    private static void open() throws IOException {

        ServerSocket sentinel = new ServerSocket(26379);
        System.out.println("java版的哨兵服务启动成功："+26379);
        Socket socket;
        while ((socket=sentinel.accept())!=null){
            try{
                while (true){
                    System.out.println("一个连接-----");
                    InputStream inputStream = socket.getInputStream();
                    byte[] request = new byte[1024];
                    inputStream.read(request);
                    //根据resp解析请求
                    String req=new String(request);
                    System.out.println("收到请求："+req);
                    System.out.println("打印结束################################");
                    String[] strings = req.split("\r\n");

                    if("get-master-addr-by-name".equals(strings[4])){
                        //返回结果

                        String result="*2\r\n"+
                                "$9\r\n"+   //127.0.0.1的参数长度
                                master.split(":")[0]+"\r\n"+
                                "$4\r\n"+
                                master.split(":")[1]+"\r\n";
                        socket.getOutputStream().write(result.getBytes());
                    }
                }
            }catch (Exception e){

            }



        }




    }

    private static void checkBadserver() {
        Iterator<String> iterator = badRedisServers.iterator();
        //选举算法
        while (iterator.hasNext()){
            String bad = iterator.next();
            try{
                Jedis jedis = getJedisByHostAndPort(bad);
                jedis.ping();
                //如果ping通，则挂在当前master
                jedis.slaveof(master.split(":")[0],Integer.parseInt(master.split(":")[1]));
                jedis.close();

                slaveRedisServers.add(bad);
                iterator.remove();
                System.out.println(bad+" 恢复正常，当前master："+master);
                break;
            }catch (Exception e){
            }



        }

    }


    /**
     *
     * 以下内容为：jedis.info("replication");
     *# Replication
     * role:master connected_slaves:2
     * slave0:ip=127.0.0.1,port=6382,state=online,offset=89022,lag=1
     * slave1:ip=127.0.0.1,port=6381,state=online,offset=89022,lag=0
     * master_repl_offset:89022
     * repl_backlog_active:1 repl_backlog_size:1048576
     * repl_backlog_first_byte_offset:2 repl_backlog_histlen:89021

     *
     */
    private static void updateSlave() {
        //获取所有slave
        try{
            Jedis jedis = getJedisByHostAndPort(master);
            //获取master主从集群信息
            String info = jedis.info("replication");
            //解析info
            String[] split = info.split("\r\n");
          /*  System.out.println(" jedis.info(\"replication\")获取的信息如下：----");
            for (String s:split
                 ) {
                System.out.print(s+" ");
            }
            System.out.println();*/
            //解析info
            int slaveCount = Integer.parseInt(split[2].split(":")[1]);
            if(slaveCount>0){
                slaveRedisServers.clear();
                for (int i = 0; i <slaveCount ; i++) {
                    String port=split[3+i].split(",")[1].split("=")[1];
                    slaveRedisServers.add("127.0.0.1:"+port);
                }
            }
            System.out.println("更新slave列表："+ Arrays.toString(slaveRedisServers.toArray(new String[]{})));
            jedis.close();
        }catch (Exception e){
            e.printStackTrace();
            System.err.println("更新slave失败："+e.getMessage());
        }
    }

    private static void checkMaster() {
        //主从切换
        //检查状态
        System.out.println("检查master状态："+master);
        try {
            Jedis jedis = getJedisByHostAndPort(master);
            jedis.ping();
            jedis.close();

        }catch (Exception e){
            System.err.println("master："+master+"................挂了");
            //master挂了
            badRedisServers.add(master);
            //切换master
            changemaster();



        }

    }

    private static void changemaster() {
        Iterator<String> iterator = slaveRedisServers.iterator();
        //选举算法
        while (iterator.hasNext()){
            String slave = iterator.next();
            try{
                Jedis jedis = getJedisByHostAndPort(slave);
                jedis.slaveofNoOne();
                jedis.close();
                master=slave;
                System.out.println("产生新的master："+master);
                break;
            }catch (Exception e){
                badRedisServers.add(slave);
            }finally {
                iterator.remove();
            }



        }
        //所有的slave切换到新的master
        for (String slave:slaveRedisServers
             ) {
            Jedis jedis = getJedisByHostAndPort(slave);
            jedis.slaveof(master.split(":")[0],Integer.parseInt(master.split(":")[1]));
            jedis.close();
        }

    }

    /**
     * 根据host+port获取jedis对象
     * @param slave
     * @return
     */
    private static Jedis getJedisByHostAndPort(String slave) {
        String slaveHost=slave.split(":")[0];
        Integer slavePort=Integer.parseInt(slave.split(":")[1]);
        return new Jedis(slaveHost, slavePort);
    }
}
