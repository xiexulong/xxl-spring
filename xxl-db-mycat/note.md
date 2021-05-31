读写分离动态配置


```

spring:
  datasource:
    select:
#有些版本用的是jdbc-url
      url: jdbc:mysql://localhost:3306/xxl_jdbc2?useUnicode=true&characterEncoding=utf8
      driver-class-name: com.mysql.cj.jdbc.Driver
      username: root
      password: helloroot
    update:
      url: jdbc:mysql://localhost:3306/xxl_jdbc2?useUnicode=true&characterEncoding=utf8
      driver-class-name: com.mysql.cj.jdbc.Driver
      username: root
      password: helloroot
```
 

create table mycat_user (
    id                  bigint unsigned primary key auto_increment,
    name         varchar(100) not null
);