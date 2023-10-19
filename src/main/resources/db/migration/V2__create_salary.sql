CREATE TABLE salary
(
    id          uuid        NOT NULL,
    amount      int4        NOT NULL,
    description varchar(30) NULL,
    chat_id     int8        NOT NULL,
    create_date timestamp   NOT NULL,
    is_send     boolean     NOT NULL DEFAULT false,
    CONSTRAINT salary_pkey PRIMARY KEY (id)
);
