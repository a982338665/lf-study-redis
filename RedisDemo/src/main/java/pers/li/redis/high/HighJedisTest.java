package pers.li.redis.high;

import org.junit.Test;
import pers.li.redis.DefineRedisClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.Set;

/**
 * create by lishengbo on 2018-05-10 11:23
 * jedis测试
 */
public class HighJedisTest {

    @Test
    public void jedis() throws Exception{

        Jedis jedis = new Jedis("127.0.0.1", 19000);
        jedis.set("a","jone-a");
        String ss = jedis.get("hello");
        jedis.close();
        Thread.sleep(1000);
        System.out.println("--+++++++++++++"+ss);
        Jedis jedis1 = new Jedis("127.0.0.1", 19000);
        jedis1.set("ab","jone-as");
        String ss1 = jedis.get("hello");
        jedis1.close();
        Thread.sleep(1000);
        System.out.println("--+++++++++++++"+ss1);
        Jedis jedis2 = new Jedis("127.0.0.1", 19000);
        jedis2.set("asd","jone-asd");
        String ss2 = jedis.get("hello");
        jedis2.close();
        Thread.sleep(1000);
        System.out.println("--+++++++++++++"+ss2);
        Jedis jedis3 = new Jedis("127.0.0.1", 19000);
        jedis3.set("asdf","jone-asdf");
        String ss3 = jedis.get("hello");
        jedis.close();
        Thread.sleep(1000);
        System.out.println("--+++++++++++++"+ss3);





//        Set<String> keys = jedis.keys("*");
//        for (String  s:
//             keys) {
//            System.out.println(s+"|"+jedis.get(s));
//        }


    }

}
