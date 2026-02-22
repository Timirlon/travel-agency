-- DML


MERGE INTO app_user (id, username, password, role, phone_number, balance, active)
VALUES ('123e4567-e89b-12d3-a456-426614174000', 'admin', 'admin', 'ADMIN', '+380999999999', 0.0, TRUE);

MERGE INTO voucher (
    id, title, description, price, tour_type, transfer_type, hotel_type, status,
    arrival_date, eviction_date, user_id, is_hot
) VALUES (
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    'Summer Escape',
    'Beach vacation',
    599.99,
    'LEISURE',
    'PLANE',
    'FOUR_STARS',
    'REGISTERED',
    '2025-07-01',
    '2025-07-10',
    '123e4567-e89b-12d3-a456-426614174000',
    TRUE
);

COMMIT;