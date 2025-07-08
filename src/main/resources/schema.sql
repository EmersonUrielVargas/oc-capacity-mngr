CREATE TABLE IF NOT EXISTS capabilities (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255)
);


CREATE TABLE IF NOT EXISTS capacity_Bootcamp (
    id BIGSERIAL PRIMARY KEY,
    id_capacity BIGINT NOT NULL,
    id_bootcamp BIGINT NOT NULL,

    CONSTRAINT uq_capacity_bootcamp UNIQUE (id_capacity, id_bootcamp),
    CONSTRAINT fk_capacity
        FOREIGN KEY (id_capacity)
        REFERENCES capabilities(id)
        ON DELETE CASCADE
);