DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS film_genres CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS friends CASCADE;
DROP TABLE IF EXISTS film_likes CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS review_likes CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS event_types CASCADE;
DROP TABLE IF EXISTS operations CASCADE;
DROP TABLE IF EXISTS film_directors CASCADE;
DROP TABLE IF EXISTS directors CASCADE;


CREATE TABLE IF NOT EXISTS mpa_ratings
(
    mpa_rating_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name          VARCHAR
);
CREATE TABLE IF NOT EXISTS directors
(
    director_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        VARCHAR
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

CREATE TABLE IF NOT EXISTS film_directors
(
    film_id     INTEGER REFERENCES films (film_id) ON DELETE CASCADE,
    director_id INTEGER REFERENCES directors (director_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name     VARCHAR
);

CREATE TABLE IF NOT EXISTS film_genres
(
    film_id  INTEGER REFERENCES films (film_id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES genres (genre_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users
(
    user_id  INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    VARCHAR,
    login    VARCHAR,
    name     VARCHAR,
    birthday DATE
);

CREATE TABLE IF NOT EXISTS friends
(
    user_id       INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
    friend_id     INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
    status_user   BOOLEAN,
    status_friend BOOLEAN,
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS film_likes
(
    film_id INTEGER REFERENCES films (film_id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS reviews
(
    review_id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content     VARCHAR,
    is_positive BOOLEAN,
    user_id     INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
    film_id     INTEGER REFERENCES films (film_id) ON DELETE CASCADE

);

CREATE TABLE IF NOT EXISTS review_likes
(
    user_id   INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
    review_id INTEGER REFERENCES reviews (review_id) ON DELETE CASCADE,
    is_like   BOOLEAN
);

CREATE TABLE IF NOT EXISTS event_types
(
    event_type_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name          VARCHAR
);

CREATE TABLE IF NOT EXISTS operations
(
    operation_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR
);

CREATE TABLE IF NOT EXISTS feeds
(
    feed_id        INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id         INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
    time_chronology DATE,
    event_type_id   INTEGER REFERENCES event_types (event_type_id),
    operation_id    INTEGER REFERENCES operations (operation_id),
    entity_id       INTEGER
);
CREATE TABLE IF NOT EXISTS reviews
(
    review_id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content     VARCHAR,
    is_positive BOOLEAN,
    user_id     INTEGER REFERENCES users (user_id),
    film_id     INTEGER REFERENCES films (film_id)

);

CREATE TABLE IF NOT EXISTS review_likes
(
    user_id   INTEGER REFERENCES users (user_id),
    review_id INTEGER REFERENCES reviews (review_id),
    is_like   BOOLEAN
);

CREATE TABLE IF NOT EXISTS event_types
(
    event_type_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name          VARCHAR
);

CREATE TABLE IF NOT EXISTS operations
(
    operation_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR
);

CREATE TABLE IF NOT EXISTS events
(
    event_id        INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id         INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
    time_chronology DATE,
    event_type_id   INTEGER REFERENCES event_types (event_type_id) ON DELETE CASCADE,
    operation_id    INTEGER REFERENCES operations (operation_id) ON DELETE CASCADE,
    entity_id       INTEGER
);






