use xxl_jdbc;
drop table if exists xxl_user;

create table xxl_user (
    id                  bigint unsigned primary key auto_increment,
    name         varchar(100) not null,
    age                int not null default 0
);