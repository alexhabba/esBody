CREATE TABLE payment
(
    create_date TIMESTAMP NOT NULL,
    amount            varchar(7),
    phone             varchar(12),

    CONSTRAINT payment_pkey PRIMARY KEY (create_date)
);

