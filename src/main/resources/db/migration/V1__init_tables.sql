create table "user"
(
    id       uuid not null,
    email    varchar(255),
    password varchar(255),
    primary key (id)
);

CREATE TABLE role
(
    id      uuid not null,
    name    varchar(255),
    user_id uuid,
    primary key (id),
--     todo изменить название ключа
    CONSTRAINT aggregator_owner_key_aggregator_id_aggregator_id_foreign FOREIGN KEY (user_id) REFERENCES "user" (id)
);
