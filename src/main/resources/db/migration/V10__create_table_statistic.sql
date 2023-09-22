CREATE TABLE consumption
(
    id          uuid         NOT NULL,
    amount      int8         NOT NULL,
    description varchar(200) NOT NULL,
    city        varchar(20)  NULL,
    create_date timestamp    NOT NULL,
    CONSTRAINT consumption_pkey PRIMARY KEY (id)
);