### Hbase-Docker 单机版

`Docker的安装以及Docker的使用此处就不予记录.`

1. Docker镜像的下载

    - 下载的是`hbase-1.4`,最新版的没有使用过,不予评论.

    `docker pull harisekhon/hbase:1.4`

    - 查看镜像是否下载成功.

    `docker images`

2. Docker镜像启动

    - 启动的时候,只映射出`zookeeper:2181`和`hbaseWebUI:16010`端口.
    
    `docker run -d --name mengliHbase -p 2181:2181 -p 16010:16010 harisekhon/hbase:1.4 `
    
    - 查看运行信息
    
    `docker ps`
    
    如果出现以下信息则表示启动成功.
    ```shell script
     CONTAINER ID        IMAGE                  COMMAND             CREATED             STATUS              PORTS                                               NAMES
     fa7453eab32f        harisekhon/hbase:1.4   "/entrypoint.sh"    6 seconds ago       Up 6 seconds    ...0.0.0.0:2181->2181/tcp,0.0.0.0:16010->16010/tcp...   mengliHbase
    ```
    - 也可以查看启动日志.
    
    `docker logs -f fa7453eab32f` 
    
    `fa7453eab32f`就是`docker ps`查看出的. 
   
 3. 测试以及使用
 
    - 测试镜像内部启动状态
   
    进入容器bash命令行;
    
    `docker exec -it fa7453eab32f bash`
    
    查看所有的java进程;
    
    `ps -ef | grep java`
    
    应该包含这几个:`-Dproc_zookeeper;-Dproc_master;-Dproc_regionserver;`
    
    测试hbase是否可用;
    
    ```shell
    > hbase shell
    HBase Shell
    Use "help" to get list of supported commands.
    Use "exit" to quit this interactive shell.
    Version 1.4.7, r763f27f583cf8fd7ecf79fb6f3ef57f1615dbf9b, Tue Aug 28 14:40:11 PDT 2018
    
    hbase(main):001:0> list_namespace
    NAMESPACE
    default
    hbase
    2 row(s) in 0.0500 seconds
    
    hbase(main):002:0>
    ```
    
    - 测试外部端口状态
    
    测试zk连接
    ```shell script
    > zkCli.sh -server localhost:2181
    ...logs
    
    [zk: localhost:2181(CONNECTED) 0] ls /
    [hbase, zookeeper]
    [zk: localhost:2181(CONNECTED) 1] ls /hbase
    [backup-masters, draining, flush-table-proc, hbaseid, master, master-maintenance, meta-region-server, namespace, online-snapshot, recovering-regions, region-in-transition, replication, rs, running, splitWAL, switch, table, table-lock]
    [zk: localhost:2181(CONNECTED) 2]
    ```
    测试webUi
    
    `直接访问 ip:16010 即可`