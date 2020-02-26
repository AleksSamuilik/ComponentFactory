
create table factory_description
(
  id bigint auto_increment
    primary key,
  phone varchar(255) null,
  position varchar(255) null
);

create table product
(
  id bigint auto_increment
    primary key,
  category varchar(255) null,
  name varchar(255) null,
  prime_cost int not null,
  type varchar(255) null
);

create table product_details
(
  id bigint auto_increment
    primary key,
  quantity int not null,
  sell_cost int not null,
  product_id bigint not null,
  constraint FKrhahp4f26x99lqf0kybcs79rb
    foreign key (product_id) references product (id)
);

create table relation_type
(
  id bigint auto_increment
    primary key,
  company_description_id bigint null,
  factory_description_id bigint null,
  constraint FK4irkj2e18x4e7dd9d7vm9p3t2
    foreign key (company_description_id) references company_description (id),
  constraint FKtngvhelyplc2u6c3c65x3wb0v
    foreign key (factory_description_id) references factory_description (id)
);

create table role
(
  id bigint auto_increment
    primary key,
  name varchar(60) null,
  constraint UK_epk9im9l9q67xmwi4hbed25do
    unique (name)
);
