create table users (
    id bigserial primary key,
    email varchar(255) unique not null,
    username varchar(100) not null,
    password_hash varchar(255) not null,
    height_cm float,
    birth_date date,
    sex varchar(10),
    activity_level varchar(30),
    created_at timestamp default now()
);
