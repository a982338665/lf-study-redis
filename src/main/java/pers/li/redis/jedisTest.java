package pers.li.redis;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.Set;

/**
 * create by lishengbo on 2018-05-10 11:23
 * jedis测试
 */
public class jedisTest {

    @Test
    public void jedis(){

        Jedis jedis = new Jedis("127.0.0.1", 6380);
//        jedis.set("hello3","jone");
        String ss = jedis.get("hello");
        Set<String> keys = jedis.keys("*");
        for (String  s:
             keys) {
            System.out.println(s+"|"+jedis.get(s));
        }
        System.out.println("--"+ss);
        jedis.close();
    }

    @Test
    public void Tojedis(){

        Jedis jedis = new Jedis("127.0.0.1", 6381);
        Set<String> keys = jedis.keys("*");
        for (String  s:
             keys) {
            System.out.println(s+"|"+jedis.get(s));
        }
        jedis.close();
    }

    /**
     * 测试结果：
     * $4 --表示4个长度的字节
     * jone
     * @throws IOException
     */
    @Test
    public void definejedis() throws IOException {

        DefineRedisClient jedis = new DefineRedisClient("127.0.0.1", 6379);
        jedis.set("hello","jone");
        String ss = jedis.get("hello");

        System.out.println("---------------");
        System.out.println(ss);
        System.out.println("_______________");
        jedis.close();
    }
}
