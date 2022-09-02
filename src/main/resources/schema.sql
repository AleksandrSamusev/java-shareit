CREATE TYPE IF NOT EXISTS bookingStatus AS ENUM ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED');

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT  GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(50),
    email varchar(50) UNIQUE
);

CREATE TABLE IF NOT EXISTS requests (
    request_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
    description text(500),
    requestor_id BIGINT REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS items (
    item_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
    name varchar(50),
    description text(500),
    is_available BOOLEAN,
    owner_id BIGINT REFERENCES users (user_id),
    request_id BIGINT REFERENCES requests (request_id)
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
    start_date TIMESTAMP WITHOUT TIME ZONE CHECK (start_date < end_date),
    end_date TIMESTAMP WITHOUT TIME ZONE CHECK (end_date > start_date),
    booker_id BIGINT REFERENCES users (user_id),
    status bookingStatus
);

CREATE TABLE IF NOT EXISTS comments (
    comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
    text text(500),
    item_id BIGINT REFERENCES items (item_id),
    author_id BIGINT REFERENCES users (user_id)
);



