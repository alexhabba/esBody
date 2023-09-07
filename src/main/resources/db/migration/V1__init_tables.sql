CREATE TABLE student
(
    id               int8         NOT NULL,
    city             varchar(10) NULL,
    course           varchar(50) NULL,
    full_name_child  varchar(50) NULL,
    full_name_parent varchar(50) NULL,
    phone            varchar(11) NULL,
    CONSTRAINT student_pkey PRIMARY KEY (id)
);

CREATE TABLE telegram_user
(
    chat_id       int8         NOT NULL,
    first_name    varchar(30) NULL,
    last_name     varchar(30) NULL,
    registered_at timestamp    NULL,
    user_name     varchar(30) NULL,
    city          varchar(10) NULL,
    CONSTRAINT telegram_user_pkey PRIMARY KEY (chat_id)
);