CREATE TABLE IF NOT EXISTS note (
    id                     VARCHAR(60)  DEFAULT RANDOM_UUID() PRIMARY KEY,
    title                   VARCHAR      NOT NULL
    );