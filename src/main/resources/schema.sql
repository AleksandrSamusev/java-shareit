DROP TABLE IF EXISTS comments;

DROP TABLE IF EXISTS bookings;

DROP TABLE IF EXISTS items;

DROP TABLE IF EXISTS requests;

DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT  GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(50),
    email varchar(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS requests (
    request_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
    description text NOT NULL ,
    requestor_id BIGINT REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS items (
    item_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
    name varchar(50) NOT NULL ,
    description text NOT NULL ,
    is_available BOOLEAN,
    owner_id BIGINT REFERENCES users (user_id),
    request_id BIGINT REFERENCES requests (request_id)
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
    start_date TIMESTAMP WITHOUT TIME ZONE CHECK (start_date < end_date),
    end_date TIMESTAMP WITHOUT TIME ZONE CHECK (end_date > start_date),
    item_id BIGINT REFERENCES items (item_id),
    booker_id BIGINT REFERENCES users (user_id),
    status varchar(50) CHECK (status IN ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED'))
);

CREATE TABLE IF NOT EXISTS comments (
    comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
    text_content TEXT NOT NULL ,
    item_id BIGINT REFERENCES items (item_id),
    author_id BIGINT REFERENCES users (user_id),
    created TIMESTAMP WITHOUT TIME ZONE
);



