

drop table if exists config;

create table config (
    id                  bigint unsigned primary key auto_increment,
    application         varchar(100) not null,
    profile             varchar(100) not null,
    label               varchar(100) not null default 'master',
    item                varchar(200) not null,
    value               varchar(5000) not null,
    type                int not null default 0,
    favorite            tinyint not null default 0,
    remark              varchar(500) not null default ''
);

create index index_config on config (application, profile, label);
