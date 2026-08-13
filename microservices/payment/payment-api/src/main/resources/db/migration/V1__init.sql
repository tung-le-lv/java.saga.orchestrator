CREATE TABLE payments (
    id              uuid NOT NULL,
    created_at      timestamp(6) with time zone,
    updated_at      timestamp(6) with time zone,
    version         integer NOT NULL,
    order_id        uuid NOT NULL,
    customer_id     uuid NOT NULL,
    amount          numeric(38, 2),
    payment_method  varchar(255),
    status          varchar(255) NOT NULL,
    transaction_id  varchar(255),
    failure_code    varchar(255),
    failure_reason  varchar(255),
    CONSTRAINT payments_pkey PRIMARY KEY (id),
    CONSTRAINT payments_status_check CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED'))
);
