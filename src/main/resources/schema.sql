CREATE TABLE IF NOT EXISTS Task (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    name VARCHAR(255) NOT NULL,
    completed BOOLEAN NOT NULL
    );