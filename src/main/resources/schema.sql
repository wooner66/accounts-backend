DROP TABLE if EXISTS users;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(60) NOT NULL,
    name VARCHAR(50) NOT NULL,
    rrn_front VARCHAR(6) NOT NULL,
    rrn_back VARCHAR(512) NOT NULL,
    rrn_hash VARCHAR(64) NOT NULL,
    phone VARCHAR(11) NOT NULL,
    top_level_address VARCHAR(40) NOT NULL,
    address_detail VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    constraint ux_users_username unique(username),
    constraint ux_users_rrn_hash unique(rrn_hash)
);

create index ix_users_rrn_front on users(rrn_front);
create index ix_users_top_level_address on users(top_level_address);
