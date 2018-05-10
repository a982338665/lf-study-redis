package pers.li.redis;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * create by lishengbo on 2018-05-10 13:53
 * 自及写的redis客户端：了解发送规则即可自己写
 * java的网络编程通过socket做
 */
public class DefineRedisClient {


    private Socket socket;
    private InputStream inputStream;
    private OutputStream onputStream;

    public DefineRedisClient(String host,int port) throws IOException {
        socket = new Socket(host, port);
        inputStream = socket.getInputStream();
        onputStream = socket.getOutputStream();

    }

    /**
     * set hello jone
     * * jedis发送过来的文字如下：
         *3      参数数量：set hello jone
         $3      多行字符串：第一个参数长度
         SET     多行字符串：第一个参数的值
         $5
         hello
         $4
         jone
     * @return
     */
    public String set(String key,String val) throws IOException {
        //组装命令
        StringBuffer command = new StringBuffer();
        command.append("*3").append("\r\n");
        command.append("$3").append("\r\n");
        command.append("SET").append("\r\n");
        //因为中文的原因所以不取key.length
        command.append("$").append(key.getBytes().length).append("\r\n");
        command.append(key).append("\r\n");
        command.append("$").append(val.getBytes().length).append("\r\n");
        command.append(val).append("\r\n");
        return execCommand(command);

    }

    /**
     * get hello
     * @param key
     * @return
     * @throws IOException
     */
    public String get(String key) throws IOException {
        //组装命令
        StringBuffer command = new StringBuffer();
        command.append("*2").append("\r\n");
        command.append("$3").append("\r\n");
        command.append("GET").append("\r\n");
        //因为中文的原因所以不取key.length
        command.append("$").append(key.getBytes().length).append("\r\n");
        command.append(key).append("\r\n");
        return execCommand(command);


    }

    private String execCommand(StringBuffer command) throws IOException {
        //发送server
        onputStream.write(command.toString().getBytes());
        //接收server返回
        byte[] b=new byte[1024];
        inputStream.read(b);
//        return new String(b);
        String s = new String(b);
        String[] split = s.split("\r\n");
        return split[1];
    }


    public void close(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
