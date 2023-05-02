package ru.yandex.practicum.filmorate.storage.dao.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.notfound.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exeption.validate.IncorrectParamException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component("DirectorDbDao")
@RequiredArgsConstructor
@Slf4j
public class DirectorDbDao implements DirectorDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director addDirector(Director director) {
        jdbcTemplate.update("INSERT INTO directors (name) VALUES (?)",
                director.getName());
        return jdbcTemplate.queryForObject(
                "SELECT director_id, name FROM directors WHERE director_id = " +
                        "(SELECT MAX(director_id) FROM directors)",
                new DirectorMapper());
    }

    @Override
    public void addFilmDirectors(Integer filmId, Set<Director> directors) {
        StringBuilder sb = new StringBuilder("Запрос на добавление в фильм с id = ")
                .append(filmId)
                .append(" режиссеров с id = ");
        List<Object[]> batch = new ArrayList<>();
        for (Director director : directors) {
            Object[] values = new Object[]{
                    filmId, director.getId()};
            sb.append(director.getId()).append(", ");
            batch.add(values);
        }
        log.info(sb.toString());
        jdbcTemplate.batchUpdate("INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)", batch);
    }

    @Override
    public void updateFilmDirectors(Integer filmId, Set<Director> directors) {
        log.info("Запрос на обновление режиссеров фильма с id = {} (смотри следующий лог)", filmId);
        jdbcTemplate.update("DELETE FROM film_directors WHERE film_id = ?", filmId);
        addFilmDirectors(filmId, directors);
    }

    @Override
    public Director updateDirector(Director director) {
        log.info("Запрос на обновление режиссера с id = {}", director.getId());
        jdbcTemplate.update("UPDATE directors SET name = ? WHERE director_id = ? ",
                director.getName(),
                director.getId());
        return getDirector(director.getId());
    }

    @Override
    public Director getDirector(Integer id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM directors WHERE director_id = ?",
                    new DirectorMapper(),
                    id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Невозможно найти режиссёра с id - {}", id);
            throw new DirectorNotFoundException(String.format("Cannot search director by %s.", id));
        }
    }

    @Override
    public List<Director> getDirectors() {
        return jdbcTemplate.query("SELECT * FROM directors",
                new DirectorMapper());
    }

    @Override
    public void deleteDirector(Integer id) {
        getDirector(id);
        jdbcTemplate.update("DELETE FROM directors WHERE director_id = ?", id);
    }

    @Override
    public Set<Director> getFilmDirectors(Integer filmId) {
        log.info("Запрос на получение списка режиссеров фильма с id = {}", filmId);
        return new HashSet<>(jdbcTemplate.query("SELECT * FROM directors WHERE director_id IN " +
                "(SELECT director_id FROM film_directors WHERE film_id = ?)", new DirectorMapper(), filmId));
    }

    @Override
    public List<Film> getDirectorFilms(Integer directorId, String sortBy) {
        getDirector(directorId);
        if (sortBy.equals("year")) {
            log.info("Запрос на получение списка всех фильмов режиссера с id = {} отсортированного по году выпуска фильма");
            return jdbcTemplate.query("SELECT film_id, f.name as name, description, release_date, " +
                            "duration_in_minutes,  f.mpa_rating_id as mpa_rating_id, fm.name as mpa_name " +
                            "FROM films as f LEFT OUTER JOIN mpa_ratings as fm ON f.mpa_rating_id = fm.mpa_rating_id  " +
                            "WHERE film_id IN " +
                            "(SELECT DISTINCT film_id FROM film_directors WHERE director_id = ?) ORDER BY release_date",
                    new FilmMapper(),
                    directorId);
        } else if (sortBy.equals("likes")) {
            log.info("Запрос на получение списка всех фильмов режиссера с id = {} отсортированного по количеству лайков");
            return jdbcTemplate.query("SELECT film_id, f.name as name, description, release_date, " +
                            "duration_in_minutes,  f.mpa_rating_id as mpa_rating_id, fm.name as mpa_name " +
                            "FROM films as f " +
                            "LEFT OUTER JOIN mpa_ratings as fm ON f.mpa_rating_id = fm.mpa_rating_id  " +
                            "LEFT JOIN (SELECT COUNT(user_id) AS likes_amount, film_id as likes_film_id FROM film_likes GROUP BY film_id) AS l " +
                            "ON f.film_id = l.likes_film_id " +
                            "WHERE film_id IN " +
                            "(SELECT DISTINCT film_id FROM film_directors WHERE director_id = ?) " +
                            "ORDER BY likes_amount",
                    new FilmMapper(),
                    directorId);
        }
        log.warn("Неизвестный тип сортировки " + sortBy);
        throw new IncorrectParamException("Неизвестный тип сортировки");
    }
}
