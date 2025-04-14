CREATE TABLE IF NOT EXISTS task (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    name VARCHAR(255) NOT NULL,
    completed BOOLEAN NOT NULL
    );