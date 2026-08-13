-- Axon's native JPA saga/token store schema (org.axonframework.modelling.saga.repository.jpa.*,
-- org.axonframework.eventhandling.tokenstore.jpa.TokenEntry). Table/column names are fixed by
-- Axon's entity mappings - do not rename them.

CREATE TABLE saga_entry (
    saga_id         varchar(255) NOT NULL,
    revision        varchar(255),
    saga_type       varchar(255),
    serialized_saga oid,
    CONSTRAINT saga_entry_pkey PRIMARY KEY (saga_id)
);

CREATE SEQUENCE association_value_entry_seq START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE association_value_entry (
    id                 bigint NOT NULL,
    association_key    varchar(255) NOT NULL,
    association_value  varchar(255),
    saga_id            varchar(255) NOT NULL,
    saga_type          varchar(255),
    CONSTRAINT association_value_entry_pkey PRIMARY KEY (id)
);

CREATE INDEX idx_association_value_entry_saga ON association_value_entry (saga_id, saga_type);
CREATE INDEX idx_association_value_entry_lookup ON association_value_entry (saga_type, association_key, association_value);

CREATE TABLE token_entry (
    processor_name varchar(255) NOT NULL,
    segment        integer NOT NULL,
    owner          varchar(255),
    "timestamp"    varchar(255) NOT NULL,
    token          oid,
    token_type     varchar(255),
    CONSTRAINT token_entry_pkey PRIMARY KEY (processor_name, segment)
);
