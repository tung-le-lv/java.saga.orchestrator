CREATE TABLE fulfillments (
    id                  uuid NOT NULL,
    created_at          timestamp(6) with time zone,
    updated_at          timestamp(6) with time zone,
    version             integer NOT NULL,
    order_id            uuid NOT NULL,
    customer_id         uuid NOT NULL,
    shipping_address    varchar(255),
    status              varchar(255) NOT NULL,
    tracking_number     varchar(255),
    estimated_delivery  timestamp(6) with time zone,
    failure_reason      varchar(255),
    CONSTRAINT fulfillments_pkey PRIMARY KEY (id),
    CONSTRAINT fulfillments_status_check CHECK (status IN ('PENDING', 'SHIPPED', 'FAILED', 'CANCELLED'))
);

CREATE TABLE fulfillment_items (
    id             uuid NOT NULL,
    created_at     timestamp(6) with time zone,
    updated_at     timestamp(6) with time zone,
    fulfillment_id uuid,
    product_id     uuid NOT NULL,
    product_name   varchar(255) NOT NULL,
    quantity       integer NOT NULL,
    CONSTRAINT fulfillment_items_pkey PRIMARY KEY (id),
    CONSTRAINT fk_fulfillment_items_fulfillment FOREIGN KEY (fulfillment_id) REFERENCES fulfillments (id)
);
