create table course_tb (
                           created_date timestamp(6),
                           id bigint,
                           instructor_id bigint,
                           modified_date timestamp(6),
                           name varchar(100) not null,
                           description varchar(500) not null,
                           primary key (id)
)
create table course_user_tb (
                                accept boolean,
                                course_id bigint,
                                created_date timestamp(6),
                                id bigint,
                                modified_date timestamp(6),
                                user_id bigint,
                                primary key (id)
)
create table post_tb (
                         course_id bigint,
                         created_date timestamp(6),
                         modified_date timestamp(6),
                         post_id bigint,
                         user_id bigint,
                         author varchar(20) not null,
                         title varchar(100) not null,
                         content varchar(500) not null,
                         category varchar(255) check (category in ('NOTICE','QUESTION','FREE')),
                         scope varchar(255) check (scope in ('PUBLIC','PRIVATE')),
                         primary key (post_id)
)
create table reply_tb (
                          answer_num bigint,
                          created_date timestamp(6),
                          id bigint,
                          modified_date timestamp(6),
                          parent_num bigint,
                          post_id bigint,
                          ref bigint,
                          ref_order bigint,
                          step bigint,
                          user_id bigint,
                          author varchar(256) not null,
                          content varchar(256) not null,
                          primary key (id)
)
create table user_tb (
                         pending boolean,
                         created_date timestamp(6),
                         id bigint,
                         modified_date timestamp(6),
                         name varchar(20),
                         email varchar(30) not null unique,
                         password varchar(256),
                         role varchar(255) check (role in ('ROLE_STUDENT','ROLE_INSTRUCTOR','ROLE_ADMIN')),
                         primary key (id)
)
create table vm_tb (
                       control boolean,
                       node_port integer not null,
                       port integer not null,
                       scope boolean not null,
                       course_id bigint,
                       created_date timestamp(6),
                       id bigint,
                       modified_date timestamp(6),
                       user_id bigint,
                       container_id varchar(255),
                       container_key varchar(255),
                       description varchar(255),
                       image_id varchar(255),
                       password varchar(255),
                       state varchar(255),
                       vm_key varchar(255),
                       vmname varchar(255),
                       primary key (id)
)