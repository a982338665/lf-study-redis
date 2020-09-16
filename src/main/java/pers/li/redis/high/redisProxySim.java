package pers.li.redis.high;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * create by lishengbo on 2018-05-10 15:16
 * redis简单代理服务实现
 */
public class redisProxySim {

    private static List<String> servers=new ArrayList();

    static {
        servers.add("127.0.0.1:6380");
        servers.add("127.0.0.1:6381");
        servers.add("127.0.0.1:6382");
    }

    //负载均衡的简单代理实现:类似于nginx 的方向代理
    //分片存储
    //日志审查
    public static void main(String[] args) {
        //监听19000端口:
            //内部经算法进行数据分配发送
            //此处可以使用线程，nio等优化---netty实现性能更好
        ServerSocket serverSocket = null;
        Socket socket;
        try {
            serverSocket = new ServerSocket(19000);
            while ((socket=serverSocket.accept())!=null){

                while (true){
                    System.out.println("一个连接-----");
                    InputStream inputStream = socket.getInputStream();
                    byte[] request = new byte[1024];
                    inputStream.read(request);
                    //根据resp解析请求
                    String req=new String(request);
//                    System.out.println("收到请求："+req);
                    String[] strings = req.split("\r\n");
                    //获取key的长度
                    int keylen = Integer.parseInt(strings[3].split("\\$")[1]);
                    //获取key的长度取模
                    int mod=keylen%servers.size();
                    //根据取模结果获取地址
                    System.out.println("根据算法选择服务器："+servers.get(mod));
                    //获取服务详细信息
                    String[] serverInfo = servers.get(mod).split(":");
                    //代理请求(类似于nginx代理)
                    Socket client = new Socket(serverInfo[0], Integer.parseInt(serverInfo[1]));
                    client.getOutputStream().write(request);
                    //返回结果
                    byte[] response = new byte[1024];
                    client.getInputStream().read(response);
                    client.close();
                    //返回最终结果
                    socket.getOutputStream().write(response);
                    System.err.println("数据："+req.split("\r\n")+" save in "+Integer.parseInt(serverInfo[1])+new String(response));

                }






            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
