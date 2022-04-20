create table if not exists app_user
(
    id BIGSERIAL,
    api_key varchar(255),
    name varchar(255),
    primary key (id)
);

create table if not exists contact
(
    id BIGSERIAL,
    created_at timestamp,
    name varchar(255),
    phone_number varchar(255),
    surname varchar(255),
    user_id bigint not null,
    primary key (id),
    constraint fkmr27c898nvhtfwe5y05yucf3w
        foreign key (user_id) references app_user
);


