package ru.yandex.practicum.filmorate.storage.dao.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.notfound.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.GenreMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GenreDbDao implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getGenreById(Integer genreID) {
        log.info("Запрос на вывод жанра по с id = {}", genreID);
        try {
            Genre genre = jdbcTemplate.queryForObject(format(""
                    + "SELECT genre_id, name "
                    + "FROM genres "
                    + "WHERE genre_id=%d", genreID), new GenreMapper());
            return genre;
        } catch (
                EmptyResultDataAccessException e) {
            String message = String.format("Невозможно найти жанр с id = %d", genreID);
            log.error(message);
            throw new GenreNotFoundException(message);
        }
    }

    @Override
    public List<Genre> getGenres() {
        log.info("Запрос на вывод списка всех жанров");
        List<Genre> result = jdbcTemplate.query(""
                + "SELECT genre_id, name "
                + "FROM genres "
                + "ORDER BY genre_id", new GenreMapper());
        return result;

    }

    @Override
    public void deleteGenre(Integer filmID) {
        log.info("Запрос на удаление списка жанров у фильма с id = {}", filmID);
        jdbcTemplate.update(""
                + "DELETE "
                + "FROM film_genres "
                + "WHERE film_id=?", filmID);
    }

    @Override
    public void addGenres(Integer filmID, Set<Genre> genres) {
        log.info("Запрос на добавление списка жанров в фильм с id = {}", filmID);
        for (Genre genre : genres) {
            jdbcTemplate.update(""
                    + "INSERT INTO film_genres (film_id, genre_id) "
                    + "VALUES (?, ?)", filmID, genre.getId());
        }
    }

    @Override
    public void updateGenres(Integer filmID, Set<Genre> genres) {
        log.info("Запрос на обновление списка жанров фильма с id = {}", filmID);
        deleteGenre(filmID);
        addGenres(filmID, genres);
    }

    @Override
    public Set<Genre> getGenres(Integer filmID) {
        log.info("Запрос на получение списка жанров фильма с id = {}", filmID);

        Set<Genre> genres = new HashSet<>(jdbcTemplate.query(format(""
                + "SELECT f.genre_id, g.name "
                + "FROM film_genres AS f "
                + "LEFT OUTER JOIN genres AS g ON f.genre_id = g.genre_id "
                + "WHERE f.film_id=%d "
                + "ORDER BY g.genre_id", filmID), new GenreMapper()));
        return genres;
    }
}
