CREATE TABLE orders (
    id                     uuid NOT NULL,
    created_at             timestamp(6) with time zone,
    updated_at             timestamp(6) with time zone,
    version                integer NOT NULL,
    -- "value" is the CustomerId embeddable's unnamed column (no @AttributeOverride on Order.customerId)
    value                  uuid,
    status                 varchar(255) NOT NULL,
    total_amount           numeric(38, 2),
    street                 varchar(255),
    city                   varchar(255),
    state                  varchar(255),
    zip_code               varchar(255),
    country                varchar(255),
    cancellation_reason    varchar(255),
    payment_transaction_id varchar(255),
    tracking_number        varchar(255),
    CONSTRAINT orders_pkey PRIMARY KEY (id),
    CONSTRAINT orders_status_check CHECK (status IN
        ('PENDING', 'PAYMENT_PROCESSING', 'PAYMENT_COMPLETED', 'PAYMENT_FAILED',
         'FULFILLING', 'SHIPPED', 'BACK_ORDERED', 'CANCELLED', 'REFUNDED'))
);

CREATE TABLE order_items (
    id           uuid NOT NULL,
    created_at   timestamp(6) with time zone,
    updated_at   timestamp(6) with time zone,
    order_id     uuid,
    product_id   uuid NOT NULL,
    product_name varchar(255) NOT NULL,
    quantity     integer NOT NULL,
    unit_price   numeric(38, 2),
    CONSTRAINT order_items_pkey PRIMARY KEY (id),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id)
);
