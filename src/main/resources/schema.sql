DROP TABLE if EXISTS users;
DROP TABLE if EXISTS message_jobs;
DROP TABLE if EXISTS job_chunks;
DROP TABLE if EXISTS send_logs;

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
    CONSTRAINT ux_users_username UNIQUE (username),
    CONSTRAINT ux_users_rrn_hash UNIQUE (rrn_hash)
);

CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX ix_users_rrn_front ON users(rrn_front);
CREATE INDEX ix_users_top_level_address ON users(top_level_address);

CREATE TABLE message_jobs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  requested_by VARCHAR(100),
  message VARCHAR(1000),
  status VARCHAR(20) DEFAULT 'PENDING',
  total_targets BIGINT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE job_chunks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id BIGINT,
    start_id BIGINT,
    end_id BIGINT,
    chunk_size INT,
    status VARCHAR(20) DEFAULT 'PENDING',
    worker_id VARCHAR(100),
    error VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE send_logs (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   job_id BIGINT,
   user_id BIGINT,
   channel VARCHAR(20),
   status VARCHAR(20),
   response VARCHAR(2000),
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
