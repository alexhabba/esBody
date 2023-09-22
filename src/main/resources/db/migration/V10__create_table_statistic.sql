CREATE TABLE consumption
(
    id          uuid         NOT NULL,
    amount      int8         NOT NULL,
    description varchar(200) NOT NULL,
    city        varchar(20)  NULL,
    create_date timestamp    NOT NULL,
    CONSTRAINT consumption_pkey PRIMARY KEY (id)
);

CREATE TABLE administrator_work_day
(
    id          uuid        NOT NULL,
    "name"      varchar(20) NULL,
    create_date timestamp   NOT NULL,
    chat_id     int8        NOT NULL,
    CONSTRAINT administrator_work_day_pkey PRIMARY KEY (id)
);

