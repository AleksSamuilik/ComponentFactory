create table user_description
(
  id bigint auto_increment
    primary key,
  relation_type_id bigint not null,
  role_id bigint not null,
  constraint UK_g8hxitlspu1htpjovfgmh5ncy
    unique (relation_type_id),
  constraint FKb4w5w0451t33srxn40n3llrh2
    foreign key (role_id) references role (id),
  constraint FKo669cum9tud0r28mwsdaxjee8
    foreign key (relation_type_id) references relation_type (id)
);

create table users
(
  id bigint auto_increment
    primary key,
  email varchar(255) null,
  full_name varchar(255) null,
  user_description_id bigint not null,
  constraint UK6dotkott2kjsp8vw4d0m25fb7
    unique (email),
  constraint FK6516o2ejwmjihid32aheccneq
    foreign key (user_description_id) references user_description (id)
);

create table auth_info_entity
(
  id bigint auto_increment
    primary key,
  login varchar(255) null,
  password varchar(100) null,
  users_id bigint not null,
  constraint FKsdi8if0gdon40f5sjikmh7qsr
    foreign key (users_id) references users (id)
);

create table orders
(
  id bigint auto_increment
    primary key,
  cost bigint not null,
  end_date date not null,
  start_date date not null,
  status varchar(255) not null,
  users_id bigint not null,
  constraint FKe6k45xxoin4fylnwg2jkehwjf
    foreign key (users_id) references users (id)
);

create table orders_product_details
(
  orders_id bigint not null,
  product_details_id bigint not null,
  constraint UK_kerp7hlpiax4mmes09iy3l7jg
    unique (product_details_id),
  constraint FK2lmwu6i3ib3nmtqap1edng86a
    foreign key (orders_id) references orders (id),
  constraint FK6a89kff09t8y287j041bfgb4o
    foreign key (product_details_id) references product_details (id)
);
