package pers.li.redis;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * create by lishengbo on 2018-05-10 11:34
 * 使用jedis发送socket接收
 * ————————————————————————
 * 以下是socket接收到的信息：（实际上模拟redis服务端）
 * jedis发送过来的文字如下：
    *3      参数数量：set hello jone
    $3      多行字符串：第一个参数长度
    SET     多行字符串：第一个参数的值
    $5
    hello
    $4
    jone
 * ————————————————————————
 * redis通信协议：resp
 * + 单行信息
 * - 错误信息
 * : 整形数字
 * $ 多行字符串  底层传输时："$6\r\nfoot\r\n"
 * * 数组
 * ————————————————————————
 * 客户端命令：
 * 127.0.0.1:6379> set name runoob
 *      OK  --显示OK，底层传输时为："+OK\r\n"
 * 127.0.0.1:6379> smember runnoob --输入未知命令报错
 *      (error) ERR unknown command 'smember' --底层显示：-开头+...
 * 127.0.0.1:6379> del a
 *      1   --底层传输时为：":1\r\n"
 *
 *
 */
public class serverSocket {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket=new ServerSocket(6378);

        Socket accept = serverSocket.accept();

        byte[] bytes = new byte[1024];

        InputStream inputStream = accept.getInputStream();

        inputStream.read(bytes);

        System.out.println(new String (bytes));
    }
}
