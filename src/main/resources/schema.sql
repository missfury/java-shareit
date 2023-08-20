DROP TABLE IF EXISTS users, requests, items, comments, bookings;
CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
    );

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR(1000) NOT NULL,
    requester_id BIGINT REFERENCES users (id),
    created TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT PK_REQUEST PRIMARY KEY (id),
    CONSTRAINT FK_REQUESTS_USERS_REQUESTER_ID foreign key (requester_id) references users
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(500) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    is_available boolean NOT NULL,
    owner_id BIGINT NOT NULL,
    request_id BIGINT,
    CONSTRAINT pk_items PRIMARY KEY (id),
    CONSTRAINT FK_ITEM_OWNER FOREIGN KEY (owner_id) REFERENCES users
    on delete cascade on update cascade,
    CONSTRAINT FK_ITEM_REQUEST FOREIGN KEY (request_id) references requests
    on delete cascade on update cascade
    );

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status varchar NOT NULL,
    CONSTRAINT PK_BOOKING PRIMARY KEY (id),
    CONSTRAINT FK_BOOKING_ITEM FOREIGN KEY (item_id) REFERENCES items
    on delete cascade on update cascade,
    CONSTRAINT FK_BOOKING_BOOKER FOREIGN KEY (booker_id) REFERENCES users
    on delete cascade on update cascade
    );

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text VARCHAR(1000) NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT PK_COMMENT PRIMARY KEY (id),
    CONSTRAINT FK_COMMENT_ITEM FOREIGN KEY (item_id) REFERENCES items
    on delete cascade on update cascade,
    CONSTRAINT FK_COMMENT_AUTHOR FOREIGN KEY (author_id) REFERENCES users
    on delete cascade on update cascade
    );


