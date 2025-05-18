DROP TABLE IF EXISTS task;
DROP TABLE IF EXISTS app_user;

CREATE TABLE app_user (
                          id UUID PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          date_of_birth DATE NOT NULL
);

CREATE TABLE task (
                      id UUID PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      description VARCHAR(1000),
                      due_date DATE NOT NULL,
                      status VARCHAR(20) NOT NULL,
                      completed BOOLEAN NOTattoos NULL,
    -- ÚJ: Hozzáadva az archived oszlop
                      archived BOOLEAN DEFAULT FALSE NOT NULL, -- Fontos a DEFAULT FALSE
                      user_id UUID,
                      FOREIGN KEY (user_id) REFERENCES app_user(id)
);