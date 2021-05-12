实现方法

建议自己定义客户端而不是使用spring-data的配置
https://www.elastic.co/guide/en/elasticsearch/client/java-api/6.8/transport-client.html



touch /Users/user/xxl/workspace/study_2021/xxl-spring/xxl-es/src/main/resources/kibana.yml
vi /Users/user/xxl/workspace/study_2021/xxl-spring/xxl-es/src/main/resources/kibana.yml


https://elk-docker.readthedocs.io/


//该镜像是在基础镜像（phusion/baseimage  最小Ubuntu基本映像） 上进行安装的
//开源地址  https://github.com/spujadas/elk-docker/blob/master/Dockerfile
docker run -p 5601:5601 -p 9200:9200 -p 5044:5044 -it --name xxl-elk sebp/elk