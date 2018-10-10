
#下载地址：https://redis.io/
#资源地址：http://download.redis.io/releases/redis-4.0.11.tar.gz

**1.安装：**
    
    1.cd /usr/local/redis
    2.wget http://download.redis.io/releases/redis-4.0.11.tar.gz
    3.tar -zxvf redis-4.0.11.tar.gz
    4.cd redis-4.0.11
    5.make -j 4 或者直接make   （-j 4表示4核运行） --编译
    6.make install                               --安装
    7.whereis redis-cli                          --验证
    8.whereis redis-server                       --验证
    
    9.修改配置文件：redis.conf
        daemonize yes       --允许后台运行
        bind 0.0.0.0        --允许所有ip访问
        requirepass root    --设置认证密码
        databases 16        --默认16个数据库，分别为0-15，默认选择0
    10.redis-server ./redis.conf     指定配置文件并启动redis
    11.ps -ef |grep redis            查看redis服务
    
    12.redis-cli        --连接redis
       127.0.0.1:6379>auth root     --密码验证
       ok
    13.redis-cli -p 6379 shutdown            --关闭redis
    13.redis-cli -p 6379 -a root shutdown            --带密码关闭redis
    14.redis-cli -h $src_ip -p $src_port -a $pwd     --带密码连接redis
    15.select 165
    16.flushdb
    
    -------------------------------------
    --将redis做成服务：
    cd redis-4.0.11/utils
    ./install_server.sh                     --执行文件，可修改执行文件，控制文件生成路径
    Welcome to the redis service installer
    This script will help you easily set up a running redis server
    
    Please select the redis port for this instance: [6379]                                                      --选择端口
    Selecting default: 6379
    Please select the redis config file name [/etc/redis/6379.conf] /usr/local/redis/redis-4.0.11/redis.conf    --指定配置文件位置
    Please select the redis log file name [/var/log/redis_6379.log] /usr/local/redis/redis-4.0.11/redis.log     --指定redis日志
    Please select the data directory for this instance [/var/lib/redis/6379] /usr/local/redis/redis-4.0.11/data --指定数据存储位置
    Please select the redis executable path [/usr/local/bin/redis-server]                                       --指定可执行目录-在make install时已经完成
    Selected config:
    Port           : 6379
    Config file    : /usr/local/redis/redis-4.0.11/redis.conf
    Log file       : /usr/local/redis/redis-4.0.11/redis.log
    Data dir       : /usr/local/redis/redis-4.0.11/data
    Executable     : /usr/local/bin/redis-server
    Cli Executable : /usr/local/bin/redis-cli
    Is this ok? Then press ENTER to go on or Ctrl-C to abort.ok                                                 --确认ok
    Copied /tmp/6379.conf => /etc/init.d/redis_6379
    Installing service...
    Successfully added to chkconfig!
    Successfully added to runlevels 345!
    /var/run/redis_6379.pid exists, process is already running or crashed
    Installation successful!
    [root@localhost utils]# 
    
    17.验证服务：chkconfig --list |grep redis
    18.centos7命令：
        --查看状态：systemctl status redis_6379
        --停止：systemctl stop redis_6379
        --启动：systemctl start redis_6379

    
        