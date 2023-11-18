CREATE TABLE lesson
(
    id          uuid        NOT NULL,
    chat_id     int8        NOT NULL,
    course      varchar(30) NULL,
    count_child int4        NOT NULL,
    create_date timestamp   NOT NULL,
    is_send     boolean     NOT NULL DEFAULT false,
    CONSTRAINT lesson_pkey PRIMARY KEY (id)
);