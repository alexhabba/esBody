CREATE TABLE consumption
(
    id          uuid         NOT NULL,
    amount      int8         NOT NULL,
    description varchar(200) NOT NULL,
    city        varchar(20)  NULL,
    create_date timestamp    NOT NULL,
    is_send     boolean      NOT NULL DEFAULT false,
    CONSTRAINT consumption_pkey PRIMARY KEY (id)
);

CREATE TABLE administrator_work_day
(
    id          uuid        NOT NULL,
    "name"      varchar(20) NULL,
    create_date timestamp   NOT NULL,
    chat_id     int8        NOT NULL,
    is_send     boolean     NOT NULL DEFAULT false,
    CONSTRAINT administrator_work_day_pkey PRIMARY KEY (id)
);

INSERT INTO consumption
(id, amount, description, city, create_date, is_send)
VALUES('242861d1-b885-4e00-99c2-b94181a02723'::uuid, 1500, 'сипуни', 'COMMON', '2023-09-28 12:25:17.062', false);
INSERT INTO consumption
(id, amount, description, city, create_date, is_send)
VALUES('11934556-2f81-419a-b578-11c78fce703a'::uuid, 4200, 'Стасу процент', 'RAMENSKOE', '2023-10-01 09:11:32.485', false);
INSERT INTO consumption
(id, amount, description, city, create_date, is_send)
VALUES('74f79fe7-70ff-42ba-9ef0-88581eedbb66'::uuid, 50000, 'аренда', 'DUBNA', '2023-09-29 14:24:14.817', false);
INSERT INTO consumption
(id, amount, description, city, create_date, is_send)
VALUES('306da46d-f1ce-487c-bb5e-883621bc54ba'::uuid, 600, 'Плакаты', 'MOSCOW', '2023-09-29 14:24:32.837', false);
INSERT INTO consumption
(id, amount, description, city, create_date, is_send)
VALUES('982c6273-a3f4-4da6-ac43-9e477ef16354'::uuid, 7642, 'Связь', 'MOSCOW', '2023-09-29 14:24:49.228', false);

INSERT INTO administrator_work_day
(id, "name", create_date, chat_id, is_send)
VALUES('6a22d662-6f1c-47e4-b056-3c648be5316a'::uuid, 'Станислав', '2023-09-28 07:28:35.514', 5032020182, false);
INSERT INTO administrator_work_day
(id, "name", create_date, chat_id, is_send)
VALUES('b11bc03b-614c-45e2-907a-18c630c42ec6'::uuid, 'Аня', '2023-09-28 09:04:21.611', 1957399392, false);
INSERT INTO administrator_work_day
(id, "name", create_date, chat_id, is_send)
VALUES('e60479f0-118a-4e18-9e0a-ea6aa2adab3f'::uuid, 'Станислав', '2023-09-29 05:43:07.993', 5032020182, false);
INSERT INTO administrator_work_day
(id, "name", create_date, chat_id, is_send)
VALUES('5a337973-7163-45db-bd73-6e4ff1b2d120'::uuid, 'Ma', '2023-09-29 06:06:49.450', 720213450, false);
INSERT INTO administrator_work_day
(id, "name", create_date, chat_id, is_send)
VALUES('85d55207-ccd3-4ccd-acbd-10913af67e06'::uuid, 'Полина', '2023-09-29 06:45:36.602', 1281122193, false);
INSERT INTO administrator_work_day
(id, "name", create_date, chat_id, is_send)
VALUES('d619a4f7-3175-4de8-aed0-efdffb26ed89'::uuid, 'Аня', '2023-09-29 07:24:00.968', 1957399392, false);
