CREATE TABLE student
(
    id               int8        NOT NULL,
    city             varchar(20) NULL,
    course           varchar(50) NULL,
    full_name_child  varchar(50) NULL,
    full_name_parent varchar(50) NULL,
    name_adder       varchar(20) NULL,
    phone            varchar(11) NULL,
    is_send          boolean     NOT NULL DEFAULT false,
    CONSTRAINT student_pkey PRIMARY KEY (id)
);

CREATE TABLE telegram_user
(
    chat_id                   int8        NOT NULL,
    first_name                varchar(30) NULL,
    last_name                 varchar(30) NULL,
    registered_at             timestamp   NULL,
    user_name                 varchar(30) NULL,
    "role"                    varchar(20) NULL,
    is_send_button_start_work boolean     NOT NULL DEFAULT false,
    CONSTRAINT telegram_user_pkey PRIMARY KEY (chat_id)
);