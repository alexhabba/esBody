CREATE TABLE consumption
(
    id          uuid         NOT NULL,
    amount      int8         NOT NULL,
    description varchar(2000) NOT NULL,
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

INSERT INTO bot.administrator_work_day
(id, "name", create_date, chat_id, is_send)
VALUES('6a22d662-6f1c-47e4-b056-3c648be5316a'::uuid, 'Станислав', '2023-09-28 07:28:35.514', 5032020182, false);
INSERT INTO bot.administrator_work_day
(id, "name", create_date, chat_id, is_send)
VALUES('b11bc03b-614c-45e2-907a-18c630c42ec6'::uuid, 'Аня', '2023-09-28 09:04:21.611', 1957399392, false);
INSERT INTO bot.administrator_work_day
(id, "name", create_date, chat_id, is_send)
VALUES('e60479f0-118a-4e18-9e0a-ea6aa2adab3f'::uuid, 'Станислав', '2023-09-29 05:43:07.993', 5032020182, false);
INSERT INTO bot.administrator_work_day
(id, "name", create_date, chat_id, is_send)
VALUES('5a337973-7163-45db-bd73-6e4ff1b2d120'::uuid, 'Ma', '2023-09-29 06:06:49.450', 720213450, false);
INSERT INTO bot.administrator_work_day
(id, "name", create_date, chat_id, is_send)
VALUES('85d55207-ccd3-4ccd-acbd-10913af67e06'::uuid, 'Полина', '2023-09-29 06:45:36.602', 1281122193, false);
INSERT INTO bot.administrator_work_day
(id, "name", create_date, chat_id, is_send)
VALUES('d619a4f7-3175-4de8-aed0-efdffb26ed89'::uuid, 'Аня', '2023-09-29 07:24:00.968', 1957399392, false);
INSERT INTO bot.administrator_work_day
(id, "name", create_date, chat_id, is_send)
VALUES('27bf31c2-79b3-4b87-a525-7c8506aceeb6'::uuid, 'Александра', '2023-10-02 05:58:55.145', 6238246042, false);
INSERT INTO bot.administrator_work_day
(id, "name", create_date, chat_id, is_send)
VALUES('f4868912-c381-4c0d-b077-26113dec3df4'::uuid, 'Дарья', '2023-10-02 06:17:12.454', 5586477666, false);
INSERT INTO bot.administrator_work_day
(id, "name", create_date, chat_id, is_send)
VALUES('36d5412b-cf06-4207-83ab-e713583bb232'::uuid, 'Полина', '2023-10-02 06:56:12.499', 1281122193, false);
INSERT INTO bot.administrator_work_day
(id, "name", create_date, chat_id, is_send)
VALUES('078ab6e9-0c6c-4cb5-8347-5f6a11737aa2'::uuid, 'Александра', '2023-10-03 05:59:03.677', 6238246042, false);
INSERT INTO bot.administrator_work_day
(id, "name", create_date, chat_id, is_send)
VALUES('016a08b8-ef75-46a7-ac79-0604d211f6ce'::uuid, 'Дарья', '2023-10-03 06:42:27.030', 5586477666, false);
INSERT INTO bot.administrator_work_day
(id, "name", create_date, chat_id, is_send)
VALUES('509c20d6-f5e8-4368-aa71-301003937be2'::uuid, 'Полина', '2023-10-03 13:49:31.338', 1281122193, false);
INSERT INTO bot.administrator_work_day
(id, "name", create_date, chat_id, is_send)
VALUES('2949df6b-dd5e-4b74-aab4-aa55b90e7847'::uuid, 'Ma', '2023-10-04 06:06:51.270', 720213450, false);
INSERT INTO bot.administrator_work_day
(id, "name", create_date, chat_id, is_send)
VALUES('831cadf0-e6f2-4853-a440-5350dfb2c6b5'::uuid, 'Дарья', '2023-10-04 06:41:18.232', 5586477666, false);


INSERT INTO bot.consumption
(id, amount, description, city, create_date, is_send)
VALUES('242861d1-b885-4e00-99c2-b94181a02723'::uuid, 1500, 'сипуни', 'COMMON', '2023-09-28 12:25:17.062', false);
INSERT INTO bot.consumption
(id, amount, description, city, create_date, is_send)
VALUES('74f79fe7-70ff-42ba-9ef0-88581eedbb66'::uuid, 50000, 'аренда', 'DUBNA', '2023-10-02 14:24:14.817', false);
INSERT INTO bot.consumption
(id, amount, description, city, create_date, is_send)
VALUES('d5677297-3cf1-4678-856a-145090817a75'::uuid, 410, 'печать сертификатов доставка воды', 'DUBNA', '2023-10-03 17:37:02.697', false);
INSERT INTO bot.consumption
(id, amount, description, city, create_date, is_send)
VALUES('982c6273-a3f4-4da6-ac43-9e477ef16354'::uuid, 7642, 'Связь', 'MOSCOW', '2023-09-29 14:24:49.228', false);
INSERT INTO bot.consumption
(id, amount, description, city, create_date, is_send)
VALUES('a45ed9c2-8135-4122-a274-5f391dbd01d5'::uuid, 60000, 'Аренда', 'MOSCOW', '2023-10-02 18:18:04.391', false);
INSERT INTO bot.consumption
(id, amount, description, city, create_date, is_send)
VALUES('5e42e810-47b7-4e20-844a-175ac2b84710'::uuid, 540, 'мусорные мешки, бумажные полотенца', 'MOSCOW', '2023-10-03 10:36:14.253', false);
INSERT INTO bot.consumption
(id, amount, description, city, create_date, is_send)
VALUES('e3c7a37c-d914-44d1-9c10-0e431513ad18'::uuid, 166, 'маркеры для доски', 'MOSCOW', '2023-10-03 17:36:26.105', false);
INSERT INTO bot.consumption
(id, amount, description, city, create_date, is_send)
VALUES('306da46d-f1ce-487c-bb5e-883621bc54ba'::uuid, 600, 'Плакаты', 'MOSCOW', '2023-09-29 14:24:32.837', false);
INSERT INTO bot.consumption
(id, amount, description, city, create_date, is_send)
VALUES('89f09d25-4f27-4af5-bc58-07d3c7000eea'::uuid, 591, '(печать и ксерокопия документов для управляющей компании, такси в ук и обратно)', 'RAMENSKOE', '2023-10-03 13:30:42.863', false);
INSERT INTO bot.consumption
(id, amount, description, city, create_date, is_send)
VALUES('11934556-2f81-419a-b578-11c78fce703a'::uuid, 4200, 'Стасу процент', 'RAMENSKOE', '2023-10-01 09:11:32.485', false);
INSERT INTO bot.consumption
(id, amount, description, city, create_date, is_send)
VALUES('382524f1-d346-4144-b49b-18c68d28e23c'::uuid, 20000, 'аренда', 'VOSKRESENSK', '2023-10-02 06:56:29.641', false);
INSERT INTO bot.consumption
(id, amount, description, city, create_date, is_send)
VALUES('23745b5e-1748-4c9d-8be4-73d124c5dcdf'::uuid, 15000, 'прозвон базы', 'VOSKRESENSK', '2023-10-02 06:59:05.112', false);
INSERT INTO bot.consumption
(id, amount, description, city, create_date, is_send)
VALUES('4f105db1-1d00-49dd-b183-5dc578b5cd67'::uuid, 500, 'уборка школы', 'VOSKRESENSK', '2023-10-03 13:34:16.855', false);
