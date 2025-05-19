CREATE TABLE client
(
    id               int8        NOT NULL,
    city             varchar(20) NULL,
    full_name        varchar(50) NULL,
    name_adder       varchar(20) NULL,
    phone            varchar(11) NULL,
    create_date      timestamp   NULL,
    is_send          boolean     NOT NULL DEFAULT false,
    CONSTRAINT client_pkey PRIMARY KEY (id)
);

CREATE TABLE telegram_user
(
    chat_id                   int8        NOT NULL,
    first_name                varchar(30) NULL,
    last_name                 varchar(30) NULL,
    registered_at             timestamp   NULL,
    user_name                 varchar(30) NULL,
    "role"                    varchar(20) NULL,
    phone                     varchar(30) NULL,
    bank                      varchar(30) NULL,
    is_send_button_start_work boolean     NOT NULL DEFAULT false,
    is_send                   boolean     NOT NULL DEFAULT false,
    CONSTRAINT telegram_user_pkey PRIMARY KEY (chat_id)
);

CREATE TABLE qr
(
    id          uuid         NOT NULL,
    qrc_id      varchar(50)  NULL,
    purpose     varchar(255) NULL,
    amount      int8         NOT NULL,
    status      varchar(20)  NULL,
    name_adder  varchar(20)  NULL,
    client_id   int8         NOT NULL,
    create_date timestamp    NOT NULL,
    is_send     boolean      NOT NULL DEFAULT false,
    update_date timestamp    NULL,
    CONSTRAINT qr_pkey PRIMARY KEY (id),
    CONSTRAINT client_qr_fk FOREIGN KEY (client_id) REFERENCES client (id)
);

CREATE TABLE consumption
(
    id          uuid          NOT NULL,
    amount      int8          NOT NULL,
    description varchar(2000) NOT NULL,
    city        varchar(20)   NULL,
    create_date timestamp     NOT NULL,
    is_send     boolean       NOT NULL DEFAULT false,
    CONSTRAINT consumption_pkey PRIMARY KEY (id)
);
