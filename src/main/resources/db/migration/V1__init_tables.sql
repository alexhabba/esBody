CREATE TABLE student
(
    id               int8 NOT NULL,
    city             varchar(255) NULL,
    course           varchar(255) NULL,
    full_name_child  varchar(255) NULL,
    full_name_parent varchar(255) NULL,
    phone            varchar(255) NULL,
    CONSTRAINT student_2_pkey PRIMARY KEY (id)
);

CREATE TABLE telegram_user
(
    chat_id       int8 NOT NULL,
    first_name    varchar(255) NULL,
    last_name     varchar(255) NULL,
    registered_at timestamp NULL,
    user_name     varchar(255) NULL,
    CONSTRAINT telegram_user_pkey PRIMARY KEY (chat_id)
);