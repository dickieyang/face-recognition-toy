create table person (
  id bigint auto_increment primary key ,
  name varchar(30),
  gender tinyint(1),
  label int(11),
  create_time TIMESTAMP default current_timestamp
) engine InnoDB;
create table person_image (
    id bigint auto_increment primary key ,
    image_path varchar(300),
    label int(11),
    person_id bigint,
    create_time TIMESTAMP default current_timestamp
) engine InnoDB;