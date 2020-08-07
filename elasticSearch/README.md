### ES-Docker 单机版

`Docker的安装以及Docker的使用此处就不予记录.`

1. Docker镜像的下载

    - 下载的是`es-7.2.0`.

    `docker pull elasticsearch:7.2.0`

    - 查看镜像是否下载成功.

    `docker images`

2. Docker镜像启动

    - 启动的时候,需要映射出`9200`和`9300`端口.
    
    `docker run --name mengliElasticSearch -p 9200:9200 -p 9300:9300  -e "discovery.type=single-node" -d elasticsearch:7.2.0`
    
    - 查看运行信息
    
    `docker ps`
    
    如果出现以下信息则表示启动成功.
    ```shell script
    CONTAINER ID        IMAGE                  COMMAND                  CREATED             STATUS              PORTS                                                    NAMES
    305ec7c29269        elasticsearch:7.2.0    "/usr/local/bin/dock…"   4 seconds ago       Up 4 seconds        0.0.0.0:9200->9200/tcp, 0.0.0.0:9300->9300/tcp           mengliElasticSearch 
    ```
    - 也可以查看启动日志.
    
    `docker logs -f mengliElasticSearch` 
    
    `mengliElasticSearch`就是这个镜像启动时候的`--name` 参数. 
   
 3. 测试
 
    - 测试外部端口状态
    
    ```shell script
    [root@mengli ~]# curl localhost:9200
    {
      "name" : "305ec7c29269",
      "cluster_name" : "docker-cluster",
      "cluster_uuid" : "vfts5iz_S32wzsFa_FY7qw",
      "version" : {
        "number" : "7.2.0",
        "build_flavor" : "default",
        "build_type" : "docker",
        "build_hash" : "508c38a",
        "build_date" : "2019-06-20T15:54:18.811730Z",
        "build_snapshot" : false,
        "lucene_version" : "8.0.0",
        "minimum_wire_compatibility_version" : "6.8.0",
        "minimum_index_compatibility_version" : "6.0.0-beta1"
      },
      "tagline" : "You Know, for Search"
    }

    ```
 4. 修改配置
    
    - 解决跨域访问问题
     
    进入到容器中
    
    `docker exec -it mengliElasticSearch /bin/bash`
    
    修改`elasticsearch.yml`文件
    ```shell script
    > find / -name elasticsearch.yml
    /usr/share/elasticsearch/config/elasticsearch.yml
    > vi /usr/share/elasticsearch/config/elasticsearch.yml
    > cat /usr/share/elasticsearch/config/elasticsearch.yml
    cluster.name: "docker-cluster"
    network.host: 0.0.0.0
    # 添加下面两个参数
    http.cors.enabled: true
    http.cors.allow-origin: "*"
    ```
    修改配置后重启容器即可。
    
    `docker restart mengliElasticSearch`
    
 5. 安装Kibana和IK分词器
    
    - Kibana的安装
    
        - 下载镜像
        
        `docker pull kibana:7.2.0` 
        
        Kibana的版本必须要跟Es的版本一致。
    
        - 运行
        
        `docker run --name mengliKibana --link=mengliElasticSearch:elasticsearch -p 5601:5601 -d kibana:7.2.0`
        
        ```
        --link参数的意思是要跟后面容器通信，：后面的是别名，必须要用elasticsearch，要不然kibana连接不上。
        或者不适用--link，使用：
        -e ELASTICSEARCH_URL=http://ip:9200
        ```
        
        - 测试
        
        访问：`localhost:5601`
     
    - IK分词器的安装
    
        es自带的分词器对中文分词不是很友好，所以安装一下IK分词器。
        
        进入容器
        
        `docker exec -it mengliElasticSearch bash`
    
        进入到plugins目录中下载分词器，下载完成后然后解压，再重启es即可。
        
        ```shell script
        > cd /usr/share/elasticsearch/plugins
        # elasticsearch的版本和ik分词器的版本需要保持一致，不然在重启的时候会失败。
        > elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.2.0/elasticsearch-analysis-ik-7.2.0.zip
        -> Downloading https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.2.0/elasticsearch-analysis-ik-7.2.0.zip
        [=================================================] 100%??
        @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        @     WARNING: plugin requires additional permissions     @
        @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        * java.net.SocketPermission * connect,resolve
        See http://docs.oracle.com/javase/8/docs/technotes/guides/security/permissions.html
        for descriptions of what these permissions allow and the associated risks.
        
        Continue with installation? [y/N]y
        -> Installed analysis-ik
        > docker restart mengliElasticSearch
        ```
       测试：
       
       进入Kibana中。
      ```shell script
       # 直接访问
       > curl localhost:9200/_cat/plugins
       305ec7c29269 analysis-ik 7.2.0
       
      # kibana中测试 
       PUT test
       
       POST test/_analyze
        {
          "analyzer": "ik_max_word",
          "text": "hello我叫mengli"
        }
      
      # 返回
      {
        "tokens" : [
          {
            "token" : "hello",
            "start_offset" : 0,
            "end_offset" : 5,
            "type" : "ENGLISH",
            "position" : 0
          },
          {
            "token" : "我",
            "start_offset" : 5,
            "end_offset" : 6,
            "type" : "CN_CHAR",
            "position" : 1
          },
          {
            "token" : "叫",
            "start_offset" : 6,
            "end_offset" : 7,
            "type" : "CN_CHAR",
            "position" : 2
          },
          {
            "token" : "mengli",
            "start_offset" : 7,
            "end_offset" : 13,
            "type" : "ENGLISH",
            "position" : 3
          }
        ]
      }
      ```
 6. EsHead的安装
 
    - 下载镜像
    
    `docker pull mobz/elasticsearch-head:5

    - 编写`docker-compose.yml`文件之后，启动
    
    - 遇到的问题
    
    ```shell script
    1. 跨域问题：No ＇Access-Control-xxxxxx'
        由于地址和端口不一样，会出现跨域问题，解放方法见4. 修改配置
    2. 请求头错误问题: {"error":"Content-Type header [application/x-www-form-urlencoded] is not supported","status":406}
        解决办法：
          - 进入 EsHead容器内，进入 _site 目录；
          - 安装vim：apt-get update -y && apt-get install -y vim
          - 编辑 vendor.js 
            # 6886行上下 /contentType: "application/x-www-form-urlencoded 改成 
            contentType: "application/json;charset=UTF-8" 
            # 7574行上下 var inspectData = s.contentType ==`= "application/x-www-form-urlencoded" &&` 改成 
            var inspectData = s.contentType === "application/json;charset=UTF-8" &&
          - 重启EsHead
    ```