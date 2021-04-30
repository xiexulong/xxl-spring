
use xxl_spring_auth_center;
-- -----------------------------------------------------------------------------
-- Procedure Drop_All_Tables
drop procedure if exists Drop_All_Tables;
delimiter $$

create procedure Drop_All_Tables ()
begin
	SET foreign_key_checks = 0;
	drop table if exists portals;
	drop table if exists pages;
	drop table if exists urlgroups;
	drop table if exists urls;
	drop table if exists urlgroups_urls_mapping;
	drop table if exists users;
	drop table if exists roles;
	drop table if exists authorities;
	drop table if exists roles_urlgroups_mapping;
	SET foreign_key_checks = 1;
end
$$
delimiter ;

-- -----------------------------------------------------------------------------
call Drop_All_Tables();
-- -----------------------------------------------------------------------------
-- Table portals
create table if not exists portals (
  `id` int unsigned not null auto_increment,
  `name` varchar(100) null,
  `sequence` int unsigned not null,
  primary key (`id`))
engine = InnoDB auto_increment=1 default charset=utf8;

-- Table pages
create table if not exists pages (
  `id` int unsigned not null auto_increment,
  `name` varchar(100) null,
  `sequence` int unsigned not null,
  primary key (`id`))
engine = InnoDB auto_increment=1 default charset=utf8;

-- Table urlgroups
create table if not exists urlgroups (
  `id` int unsigned not null auto_increment,
  `uid` int unsigned not null,
  `name` varchar(100) null,
  `portal_id` int unsigned null,
  `page_id` int unsigned null,
  `sequence` int unsigned not null,
  primary key (`id`),
  index `fk_urlgroup_portal1_idx` (`portal_id` ASC),
  index `fk_urlgroup_page1_idx` (`page_id` ASC),
  constraint ix_urlgroup_uid unique (uid),
  constraint `fk_urlgroup_portal1`
    foreign key (`portal_id`)
    references portals (`id`)
    on delete set null
    on update cascade,
  constraint `fk_urlgroup_page1`
    foreign key (`page_id`)
    references pages (`id`)
    on delete set null
    on update cascade)
engine = InnoDB auto_increment=1 default charset=utf8;


-- Table urls
create table if not exists urls (
  `id` int unsigned not null auto_increment,
  `url_pattern` varchar(1000) null,
  `http_method` enum('GET', 'POST', 'PUT', 'DELETE', 'OPTION', 'HEAD', 'TRACE', 'ALL') null default 'get',
  primary key (`id`))
engine = InnoDB auto_increment=1 default charset=utf8;

-- Table urlgroups_urls_mapping
create table if not exists urlgroups_urls_mapping (
  `urlgroup_id` int unsigned not null,
  `url_id` int unsigned not null,
  primary key (`urlgroup_id`, `url_id`),
  index `fk_urlgroup_has_url_idx` (`url_id` asc),
  index `fk_urlgroup_has_urlgroup_idx` (`urlgroup_id` asc),
  constraint `fk_urlgroup_has_urlgroup1`
    foreign key (`urlgroup_id`)
    references urlgroups (`id`)
    on delete cascade
    on update cascade,
  constraint `fk_urlgroup_has_url1`
    foreign key (`url_id`)
    references urls (`id`)
    on delete cascade
    on update cascade)
engine = InnoDB auto_increment=1 default charset=utf8;

-- Table users
create table if not exists users(
		 `id`                 int unsigned    primary key auto_increment,
         `username` 			varchar(100)     not null,
         `password` 			varchar(100) 	not null,        
         `enabled` 			boolean 		not null,
         `display_name`		varchar(100)    null,
         `email`				varchar(100)    null,
         `added_time`         timestamp       not null default current_timestamp,
         `updated_time`   timestamp       not null default current_timestamp on update current_timestamp,
         constraint ix_username unique (username),
         constraint ix_display_name unique (display_name)
)engine=InnoDB auto_increment=1 default charset=utf8;

-- Table roles
create table if not exists roles (
	`id`               int unsigned    primary key auto_increment,
	`role`             varchar(100) 	 not null,
	`sequence`         int unsigned 	 not null,
	`removed`          boolean         not null default false,     
	`added_time`  	 timestamp       not null default current_timestamp,
	`updated_time` timestamp    not null default current_timestamp on update current_timestamp,
	constraint ix_roles_sequence unique (sequence),
	constraint ix_roles_role unique (role)
)engine=InnoDB auto_increment=1 default charset=utf8;

-- Table authorities
create table if not exists authorities (
		`id`                  int unsigned    primary key auto_increment,
		`username` 			varchar(100)  	not null,
		`authority` 			varchar(100) 	not null,       
		`added_time`          timestamp       not null default current_timestamp,
        `updated_time`    timestamp       not null default current_timestamp on update current_timestamp,
        constraint ix_auth_username unique (username,authority),
        constraint fk_authorities_users foreign key (username) references users(username) on delete cascade on update cascade,
        constraint fk_authorities_type foreign key (authority) references roles(role) on delete restrict on update cascade
)engine=InnoDB auto_increment=1 default charset=utf8;


-- Table roles_urlgroups_mapping
create table if not exists roles_urlgroups_mapping (         
	`id`			int unsigned		primary key auto_increment,         
	`urlgroup_id`   	int unsigned 	not null,                
	`role_id` 			int unsigned 		not null,                
	`granting` 		boolean 		not null,                 
	`updated_time`    	timestamp       	not null default current_timestamp on update current_timestamp, 
	constraint ix_roles_urlgroups_mapping_urlgroup_id_role_id unique (urlgroup_id,role_id),    
	constraint fk_entry_group foreign key (urlgroup_id) references urlgroups (id) on delete cascade on update cascade,         
	constraint fk_entry_role foreign key (role_id) references roles (id) on delete cascade on update cascade 
) engine=InnoDB auto_increment=1 default charset=utf8;

-- -----------------------------------------------------------------------------
-- Procedure Add_Page

drop procedure if exists Add_Portal;
delimiter $$
create procedure Add_Portal (
	in  i_name      VARCHAR(100),
	in  i_sequence  INT unsigned,
	out o_new_id  int unsigned)
begin
	insert into portals set
		name 		   = i_name,
		sequence       = i_sequence;
    set o_new_id = last_insert_id();
end;

$$
delimiter ;
-- -----------------------------------------------------------------------------



-- Procedure Add_Page
drop procedure if exists Add_Page;
delimiter $$
create procedure Add_Page (
	in  i_name      VARCHAR(100),
	in  i_sequence  INT unsigned,
	out o_new_id  int unsigned)
begin
	insert into pages set
		name 		   = i_name,
		sequence       = i_sequence;
    set o_new_id = last_insert_id();
end;

$$
delimiter ;
-- -----------------------------------------------------------------------------

-- Procedure Add_urlgroup;
drop procedure if exists Add_Urlgroup;
delimiter $$
create procedure Add_Urlgroup (
	in  i_uid  INT unsigned,
	in  i_name VARCHAR(100),
	in  i_portal_id    INT,
	in  i_page_id    INT,
	in  i_sequence  INT unsigned,
	out o_new_id  int unsigned)
begin
	insert into urlgroups set
	    uid          = i_uid,
		name 		 = i_name,
		portal_id    = i_portal_id,
		page_id      = i_page_id,
		sequence       = i_sequence;
    set o_new_id = last_insert_id();

end;

$$
delimiter ;
-- -----------------------------------------------------------------------------

-- Procedure Add_Url;
drop procedure if exists Add_Url;
delimiter $$
create procedure Add_Url (
	in  i_url_pattern VARCHAR(1000),
	in  i_http_method ENUM('GET', 'POST', 'PUT', 'DELETE', 'OPTION', 'HEAD', 'TRACE', 'ALL'),
	out o_new_id  int unsigned)
begin
	insert into urls set
		url_pattern 		= i_url_pattern,
        http_method         = i_http_method;
        set o_new_id = last_insert_id();

end;

$$
delimiter ;
-- -----------------------------------------------------------------------------
-- Procedure Add_Urlgroup_Has_Url;
drop procedure if exists Add_Urlgroup_Has_Url;
delimiter $$
create procedure Add_Urlgroup_Has_Url (
	in  i_urlgroup_id INT unsigned,
	in  i_url_id INT unsigned)
begin
	insert into urlgroups_urls_mapping set
		urlgroup_id 	  = i_urlgroup_id,
        url_id         = i_url_id;
end;

$$
delimiter ;
-- -----------------------------------------------------------------------------
-- Procedure Add_User


drop procedure if exists Add_User;
delimiter $$
create procedure Add_User (
	i_username varchar(100),         
	i_password	varchar(100),        
    i_enabled	boolean,
    i_display_name varchar(100),
    i_email varchar(100),
	out o_new_id  int unsigned)
begin
	insert into users set
		username 		= i_username,
		password 	  	= i_password,
		enabled  	  	= i_enabled,
		display_name  	= i_display_name,
        email			= i_email,
		added_time 	    = now();
    set o_new_id = last_insert_id();
end;

$$
delimiter ;
-- -----------------------------------------------------------------------------
-- Procedure Add_Role
drop procedure if exists Add_Role;
delimiter $$
create procedure Add_Role (
	i_role 			varchar(100),
	i_sequence 		int unsigned,
	i_removed       boolean,
	out o_new_id  	int unsigned)
begin
	insert into roles set
		role 			= i_role,
		sequence 	  	= i_sequence,
		removed         = i_removed,
		added_time 	    = now();
    set o_new_id = last_insert_id();
end;

$$
delimiter ;
-- -----------------------------------------------------------------------------
-- Procedure Add_Authoritie
drop procedure if exists Add_Authoritie;
delimiter $$
create procedure Add_Authoritie (
	i_username varchar(100),             
	i_authority varchar(100),
	out o_new_id  int unsigned)
begin
	insert into authorities set
		username 		= i_username,
		authority 	  	= i_authority,
		added_time 	    = now();
    set o_new_id = last_insert_id();
end;

$$
delimiter ;

-- -----------------------------------------------------------------------------

-- Procedure Add_Roles_Urlgroups_Mapping
drop procedure if exists Add_Roles_Urlgroups_Mapping;
delimiter $$
create procedure Add_Roles_Urlgroups_Mapping (
	in  i_urlgroup_id   	  int unsigned,
	in  i_role_id		  	  			  int unsigned,
	in  i_granting	  	  			  boolean,
	out o_new_id      int unsigned)
begin
	insert into roles_urlgroups_mapping set
		urlgroup_id 	 		 = i_urlgroup_id,
		role_id 	 		 	 			 = i_role_id,
		granting 	 		 			 = i_granting,
		updated_time 	    = now();
    set o_new_id = last_insert_id();
end;

$$
delimiter ;
-- -----------------------------------------------------------------------------

-- Procedure Pre_Load_Potal
drop procedure if exists Pre_Load_Potal;
delimiter $$
create procedure Pre_Load_Potal()
begin
	declare l_portal_id  int unsigned;
	declare l_page_id int unsigned;
	declare l_url_group_id1  int unsigned;
	declare l_url_group_id2  int unsigned;
	declare l_url_group_id  int unsigned;
    declare l_url_group_usermanagement_id  int unsigned;

	declare l_url_last_id  int unsigned;

	declare l_admin_role_id  int unsigned;
	declare l_user_role_id  int unsigned;

	declare l_Roles_Urlgroups_last_id  int unsigned;
	declare l_url_id  int unsigned;

		call Add_Role("ADMIN",0,false,l_admin_role_id);
		call Add_Role("USER",1,false,l_user_role_id);

	    call Add_Portal("visualization portal", 1, l_portal_id);
		call Add_Urlgroup(110000, "All Permission", l_portal_id, NULL, 1, l_url_group_id);
        call Add_Roles_Urlgroups_Mapping(l_url_group_id,l_admin_role_id,1,l_Roles_Urlgroups_last_id);
        call Add_Roles_Urlgroups_Mapping(l_url_group_id,l_user_role_id,1,l_Roles_Urlgroups_last_id);
        call Add_Url("^/(((?!exception)[^/])*).html([^/]*)$", "ALL", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
        call Add_Url("^/files/visualization/([^\\s]+)$", "ALL", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
        call Add_Url("^/2dimages/([^\\s]+)$", "ALL", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
        call Add_Url("^/Visual2DImageService/([^\\s]+)$", "ALL", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
        call Add_Url("/Visual2DImageService/developer/([^\\s&^\\?]+)$", "ALL", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
        call Add_Url("^/database/([^\\s]+)$", "ALL", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Url("^/vehicle_list/visualization([^/]*)", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Url("^/signs/visualization([^/]*)", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Url("^/lines/visualization([^/]*)", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Url("^/paints/visualization([^/]*)", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Url("^/trafficlights/visualization([^/]*)", "GET", l_url_last_id);
        call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Url("^/expected_path/visualization([^/]*)", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Url("^/trajectory/visualization([^/]*)", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Url("^/segments/visualization([^/]*)", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Url("^/rtvs/visualization([^/]*)", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Url("^/svd/visualization([^/]*)", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Url("^/rd/visualization([^/]*)", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Url("^/ep_start/visualization([^/]*)", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Url("^/ep_eid/visualization([^/]*)", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Url("^/ep_seqs/visualization([^/]*)", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Url("^/ep_locations/visualization([^/]*)", "PUT", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Url("^/trafficlights/visualization([^/]*)", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Url("^/os/rtv_laneid_coverage_count([^/]*)", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);


	    call Add_Portal("debug portal", 2, l_portal_id);
		call Add_Urlgroup(120000, "All Permission", l_portal_id, NULL, 2, l_url_group_id);
		call Add_Roles_Urlgroups_Mapping(l_url_group_id,l_admin_role_id,1,l_Roles_Urlgroups_last_id);
        call Add_Roles_Urlgroups_Mapping(l_url_group_id,l_user_role_id,1,l_Roles_Urlgroups_last_id);
        call Add_Url("^/debug/(((?!exception)[^/])*).html([^/]*)$", "ALL", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
        call Add_Url("^/debug/$", "ALL", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
        call Add_Url("^/debugdb/([^\\s]+)$", "ALL", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
        call Add_Url("^/serial/files/downloads/([^\\s]+)$", "ALL", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
        call Add_Url("^/SerializedService/([^\\s]+)$", "ALL", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
        call Add_Url("^/LogAnalyzer/api/([^\\s]+)$", "ALL", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
        call Add_Url("^/LogAnalyzer/logDownLoad/([^\\s]+)$", "ALL", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
        call Add_Url("^/storage/([^\\s]+)$", "ALL", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);


	    call Add_Portal("operation portal", 4, l_portal_id);
		call Add_Urlgroup(140000, "All Permission", l_portal_id, NULL, 3, l_url_group_id);
		call Add_Roles_Urlgroups_Mapping(l_url_group_id,l_admin_role_id,1,l_Roles_Urlgroups_last_id);
		call Add_Page("Server Management", 1, l_page_id);
		call Add_Urlgroup(140101, "Enter page", l_portal_id, l_page_id, 4, l_url_group_id1);
		call Add_Roles_Urlgroups_Mapping(l_url_group_id1,l_user_role_id,1,l_Roles_Urlgroups_last_id);
        call Add_Url("/operations/servermanagement.html", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id1, l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Urlgroup(140102, "Add server", l_portal_id, l_page_id, 5, l_url_group_id2);
		call Add_Roles_Urlgroups_Mapping(l_url_group_id2,l_user_role_id,1,l_Roles_Urlgroups_last_id);
		call Add_Urlgroup(140103, "Edit server config", l_portal_id, l_page_id, 6, l_url_group_id2);
		call Add_Roles_Urlgroups_Mapping(l_url_group_id2,l_user_role_id,1,l_Roles_Urlgroups_last_id);
		call Add_Urlgroup(140104, "Modify active status", l_portal_id, l_page_id, 7, l_url_group_id2);
		call Add_Roles_Urlgroups_Mapping(l_url_group_id2,l_user_role_id,1,l_Roles_Urlgroups_last_id);
		call Add_Urlgroup(140105, "Restart service", l_portal_id, l_page_id, 8, l_url_group_id2);
		call Add_Roles_Urlgroups_Mapping(l_url_group_id2,l_user_role_id,1,l_Roles_Urlgroups_last_id);
		call Add_Urlgroup(140106, "Deploy server", l_portal_id, l_page_id, 9, l_url_group_id2);
		call Add_Roles_Urlgroups_Mapping(l_url_group_id2,l_user_role_id,1,l_Roles_Urlgroups_last_id);

		call Add_Page("Task Monitor",2, l_page_id);
		call Add_Urlgroup(140201, "Enter page", l_portal_id, l_page_id, 10, l_url_group_id1);
		call Add_Roles_Urlgroups_Mapping(l_url_group_id1,l_user_role_id,1,l_Roles_Urlgroups_last_id);
        call Add_Url("/operations/taskmonitor.html", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id1, l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Urlgroup(140202, "Stop task", l_portal_id, l_page_id, 11, l_url_group_id2);
		call Add_Roles_Urlgroups_Mapping(l_url_group_id2,l_user_role_id,1,l_Roles_Urlgroups_last_id);
		call Add_Urlgroup(140203, "Download log file", l_portal_id, l_page_id, 12, l_url_group_id2);
		call Add_Roles_Urlgroups_Mapping(l_url_group_id2,l_user_role_id,1,l_Roles_Urlgroups_last_id);

		call Add_Page("Config Management", 3, l_page_id);
		call Add_Urlgroup(140301, "Enter page", l_portal_id, l_page_id, 13, l_url_group_id1);
		call Add_Roles_Urlgroups_Mapping(l_url_group_id1,l_user_role_id,1,l_Roles_Urlgroups_last_id);
        call Add_Url("/operations/configmanagement.html", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id1, l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
        call Add_Url("/config-center/configs", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id1, l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Urlgroup(140302, "Modify config", l_portal_id, l_page_id, 14, l_url_group_id2);
		call Add_Roles_Urlgroups_Mapping(l_url_group_id2,l_user_role_id,1,l_Roles_Urlgroups_last_id);
		call Add_Url("/config-center/configs", "PUT", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id2, l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);

		call Add_Page("Alarm Log Management",  4, l_page_id);
		call Add_Urlgroup(140401, "Enter page", l_portal_id, l_page_id, 15, l_url_group_id1);
		call Add_Roles_Urlgroups_Mapping(l_url_group_id1,l_user_role_id,1,l_Roles_Urlgroups_last_id);
        call Add_Url("/operations/alarmlog.html", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id1, l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);


		call Add_Page("User Management", 5, l_page_id);
		call Add_Urlgroup(140501, "Enter page", l_portal_id, l_page_id, 16, l_url_group_id1);

		set l_url_group_usermanagement_id = l_url_group_id1;

        call Add_Url("^/operations/$", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id1, l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
        call Add_Url("/operations/usermanagement.html", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id1, l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
        call Add_Url("/AuthCenter/api/users([^/]*)", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id1, l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Urlgroup(140502, "Add user", l_portal_id, l_page_id, 17, l_url_group_id2);
		call Add_Url("/AuthCenter/api/users([^/]*)", "POST", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id2, l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Urlgroup(140503, "Edit user", l_portal_id, l_page_id, 18, l_url_group_id2);
		call Add_Url("/AuthCenter/api/users/([^/]+)", "POST", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id2, l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
        call Add_Url("/AuthCenter/api/users/([^/]+)/password([^/]*)", "POST", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id2, l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Urlgroup(140504, "Delete user", l_portal_id, l_page_id, 19, l_url_group_id2);
		call Add_Url("/AuthCenter/api/users/([^/]+)", "DELETE", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id2, l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);


		call Add_Page("Role Management",  6, l_page_id);
		call Add_Urlgroup(140604, "Enter page", l_portal_id, l_page_id, 20, l_url_group_id1);
        call Add_Url("/operations/rolemanagement.html", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id1, l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
        call Add_Url("/AuthCenter/api/portals/urlGroupInfo([^/]*)", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id1, l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
        call Add_Url("/AuthCenter/api/roles([^/]*)", "POST", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id1, l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
        call Add_Url("/AuthCenter/api/role/([^/]+)", "PUT", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id1, l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
        call Add_Url("/AuthCenter/api/role/([^/]+)", "DELETE", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id1, l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
        call Add_Url("/AuthCenter/api/role/([^/]+)/urlGroups([^/]*)", "PUT", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id1, l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
        call Add_Url("/AuthCenter/api/roles", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id1, l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id, l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_usermanagement_id, l_url_last_id);
        
		call Add_Urlgroup(100000, "Unlimited", null, null, 21, l_url_group_id1);
        call Add_Url("(.*)/exception.html\\?type=(.*)$", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id1, l_url_last_id);
        call Add_Url("/AuthCenter/api/users/([^/]+)/enabledUrlGroupIds([^/]*)", "GET", l_url_last_id);
		call Add_Urlgroup_Has_Url(l_url_group_id1, l_url_last_id);

end

$$
delimiter ;

-- -----------------------------------------------------------------------------
-- Procedure Pre_Load_Users_Authorities
drop procedure if exists Pre_Load_Users_Authorities;
delimiter $$
create procedure Pre_Load_Users_Authorities ()
begin
	declare l_user_id  int unsigned;
	declare l_authorities_id int unsigned;
	declare l_authorities_user_type_id int unsigned;
	declare l_authorities_admin_type_id int unsigned;
	declare l_authorities_super_admin_type_id int unsigned;
	declare user_auth_loaded boolean;  
	declare l_Roles_Urlgroups_last_id  int unsigned;
		call Add_Role("SUPER_ADMIN",2,false,l_authorities_super_admin_type_id);
		call Add_User("admin","$2a$15$cJNFZHP4wa2FPg9UsV6.KegfjJUA5lnyDw0/JNcj/SkYzcrqyji16",1,"ADMIN","admin@xxl.com",l_user_id);
		call Add_User("road","$2a$15$QeuSCLP8s8r5xMJsW9xcGe8f7EeI7YJCrWlLlabbYWX2R7VBFwwXy",1,"road","road@xxl.com",l_user_id);
		call Add_Authoritie("road","USER",l_authorities_id);
		call Add_Authoritie("admin","SUPER_ADMIN",l_authorities_id);
end

$$
delimiter ;

-- -----------------------------------------------------------------------------
call Pre_Load_Potal();
call Pre_Load_Users_Authorities();
-- drop procedure if exists Add_Home_Menu;
-- drop procedure if exists Add_User;
-- drop procedure if exists Add_Authoritie;
-- drop procedure if exists Pre_Load_Home_Menu;
-- drop procedure if exists Pre_Load_Users_Authorities;
