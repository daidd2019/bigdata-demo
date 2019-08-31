## kafka 客户端操作

### kafka 客户端windows 调试
    VM 参数添加 
    -Djava.security.krb5.conf=kafka-op\src\main\conf\krb5.conf 
    -Djava.security.auth.login.config=kafka-op\src\main\conf\kafka_client_jaas.conf
    
### 使用
    export JAVA_HOME=
    请更改send.properites 和 client_jaas.conf文件
    run_send_msg.sh -t topic1 -f ../conf/send.properites
    run_read_msg.sh -t topic1 -f ../conf/read.properites