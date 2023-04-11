DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS film_genres CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS friendships CASCADE;
DROP TABLE IF EXISTS film_likes CASCADE;

CREATE TABLE IF NOT EXISTS mpa_ratings
(
    mpa_rating_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name          VARCHAR UNIQUE
);

CREATE TABLE IF NOT EXISTS films
(
    film_id             INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name                VARCHAR,
    description         VARCHAR,
    release_date        DATE,
    duration_in_minutes INTEGER,
    mpa_rating_id       INTEGER REFERENCES mpa_ratings (mpa_rating_id)
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name     VARCHAR
);

CREATE TABLE IF NOT EXISTS film_genres
(
    film_id  INTEGER REFERENCES films (film_id),
    genre_id INTEGER REFERENCES genres (genre_id)
);

CREATE TABLE IF NOT EXISTS users
(
    user_id  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    VARCHAR,
    login    VARCHAR,
    name     VARCHAR,
    birthday DATE
);

CREATE TABLE IF NOT EXISTS friends
(
    user_id   BIGINT REFERENCES users (user_id),
    friend_id INTEGER REFERENCES users (user_id),
    status    BOOLEAN,
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS film_likes
(
    film_id INTEGER REFERENCES films (film_id),
    user_id INTEGER REFERENCES users (user_id)
);




