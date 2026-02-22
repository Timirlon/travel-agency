CREATE TABLE IF NOT EXISTS app_user (
    id UUID PRIMARY KEY,
    username VARCHAR(255),
    password VARCHAR(255),
    role VARCHAR(50),
    phone_number VARCHAR(50),
    balance DECIMAL(19, 2),
    active BOOLEAN
);

CREATE TABLE IF NOT EXISTS voucher (
    id UUID PRIMARY KEY,
    title VARCHAR(255),
    description VARCHAR(255),
    price DOUBLE,
    tour_type VARCHAR(50),
    transfer_type VARCHAR(50),
    hotel_type VARCHAR(50),
    status VARCHAR(50),
    arrival_date DATE,
    eviction_date DATE,
    user_id UUID,
    is_hot BOOLEAN,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES app_user(id)
);

COMMIT;