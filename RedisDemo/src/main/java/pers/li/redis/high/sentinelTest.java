package pers.li.redis.high;

import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Set;

/**
 * create by lishengbo on 2018-05-11 08:50
 * 哨兵机制:主从服务器配置，避免单点故障，即处理master6380故障后，不能够重新选举master的问题
 */
public class sentinelTest {


    @Test
    public void jedis(){
        //配置哨兵服务器信息
        Set<String> sentinel = new HashSet<String>();
        sentinel.add("127.0.0.1:26379");
//        sentinel.add("127.0.0.1:26379");
//        sentinel.add("127.0.0.1:26379");

        JedisSentinelPool jedisSentinelPool=new JedisSentinelPool("master",sentinel);
        //去哨兵服务器查询当前master信息
        //命令：SENTINEL get-master-addr-by-name master
        Jedis jedis = jedisSentinelPool.getResource();
        //执行redis命令
        jedis.set("hello_sentinel_01","hello_sentinel_01");
        jedis.close();

    }
}
