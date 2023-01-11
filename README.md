效果图
-----
![](https://gebaocai.github.io/img/in-post/2023/android-rabbitmq/sample.gif)

一. 后端服务
------
采用RabbitMQ， 用Docker安装RabbitMQ

1. docker-compose 配置 RabbitMQ
```
version: '3.3'
services:
  rabbitmq:
    container_name: rabbitmq
    image: rabbitmq:3-management
    ports:
      - "15672:15672"
      - "5672:5672"
    networks:
      - base_net
    volumes:
      - ./conf/rabbitmq/rabbitmq-isolated.conf:/etc/rabbitmq/rabbitmq.config
networks:
  base_net:
    external: true

```
1. ./conf/rabbitmq/rabbitmq-isolated.conf
```
[
 {rabbit,
  [
   %% The default "guest" user is only permitted to access the server
   %% via a loopback interface (e.g. localhost).
   %% {loopback_users, [<<"guest">>]},
   %%
   %% Uncomment the following line if you want to allow access to the
   %% guest user from anywhere on the network.
   {loopback_users, []},
   {default_vhost,       "/"},
   {default_user,        "guest"},
   {default_pass,        "guest"},
   {default_permissions, [".*", ".*", ".*"]}
  ]}
].
```

1. 启动RabbitMQ
```
docker-compose up -d rabbitmq
```

二. Android使用技术点
------
Room+Livedata+ViewModel+RecycleView
详情请参照代码
