CREATE TABLE qr
(
    id          uuid         NOT NULL,
    qrc_id      varchar(50)  NULL,
    purpose     varchar(255) NULL,
    amount      int8         NOT NULL,
    status      varchar(20)  NULL,
    name_adder  varchar(20)  NULL,
    student_id  int8         NOT NULL,
    create_date timestamp    NOT NULL,
    is_send     boolean      NOT NULL DEFAULT false,
    CONSTRAINT qr_pkey PRIMARY KEY (id),
    CONSTRAINT student_qr_fk FOREIGN KEY (student_id) REFERENCES student (id)
);


