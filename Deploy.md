部署文档
# 服务列表

* Chorus 前端NodeJS 服务。
* NodeJS 依赖Redis服务。3.1.8版本
* Chorus Server后端服务。
* MySQL服务(Chorus后端、XD支撑库)
* Ambari集群(Hadoop、Yarn、HBase、Kafka、Zookeeper、Ranger)
* 数据实验室依赖服务。
* Titan依赖ES服务

# 部署流程

## Nodejs

略

## Redis
参考官方文档即可：[https://redis.io/download](https://redis.io/download)
```bash
wget http://download.redis.io/releases/redis-3.2.9.tar.gz
tar xzf redis-3.2.9.tar.gz
cd redis-3.2.9
make
# 启动
src/redis-server
```

## Chorus Server 后端服务

在Jenkins上配置自动化部署，可参考[Chorus-server-dev](http://jenkins.cssrv.dataengine.com/jenkins/job/chorus-server-dev/)

> 注意：需要根据不同的环境激活不同的Maven profile

## MySQL服务

Chorus后端和Spring XD都需要MySQL作为支撑库。  
Chorus数据库初始化脚本可从10.200.48.79库导出。同时，表xd_module、t_role、
environment_info、host_env、host_info、resource_template需要初始化数据。

XD库只需要建立xd schema即可。xd服务会自动初始化数据库。

## Ambari相关服务

略

## 数据实验室依赖服务

参考:（[http://wiki.dataengine.com/x/v2HK]((http://wiki.dataengine.com/x/v2HK)[)）

概括起来，

* 修改chorus-server项目里core-site.xml,hdfs-site.xml,yarn-site.xml配置项，重新打包chorus-datalab
* 需要部署拷贝chorus-datalab.jar文件到chorus服务器和hdfs
* 需要拷贝/rc/local/proxy/proxy脚本
* 修改proxy权限 r-s 使用命令: chmod u+s,g+s /rc/local/proxy/proxy
* 需要chorus服务器安装yarn-client
* 需要部署etcd、confd、nginx等服务
* 前端实验室跳转链接应为nginx服务器地址

## ElasticSearch

需要1.5.1版本

## Chorus Server 配置文件

可在src/main/profiles下增加新的文件夹，包含的文件可参考其他文件夹中的配置。
对应修改其中的服务地址即可。



