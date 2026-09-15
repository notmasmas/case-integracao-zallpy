CREATE TABLE users(
    id bigint generated always as identity primary key,
    email varchar(100) not null,
    password varchar(100) not null,
    role varchar(30) not null
)