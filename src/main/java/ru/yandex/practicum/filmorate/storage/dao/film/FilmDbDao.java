package ru.yandex.practicum.filmorate.storage.dao.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.notfound.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeption.validate.DateReleaseException;
import ru.yandex.practicum.filmorate.exeption.validate.FilmIdNotNullException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Primary
@Repository
@RequiredArgsConstructor
public class FilmDbDao implements FilmDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {

        checkAdd(film);
        jdbcTemplate.update(
                "INSERT INTO films (name, description, release_date, duration_in_minutes, mpa_rating_id)"
                        + "VALUES(?, ?, ?, ?, ?)",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId());

        Film result = jdbcTemplate.queryForObject(format(
                "SELECT film_id, f.name as name, description, release_date, " +
                        "            duration_in_minutes," +
                        "            f.mpa_rating_id as mpa_rating_id," +
                        "            fm.name as mpa_name " +
                        "FROM films as f " +
                        "LEFT OUTER JOIN mpa_ratings as fm " +
                        "ON f.mpa_rating_id = fm.mpa_rating_id  " +
                        "WHERE f.name='%s' " +
                        "AND description='%s' " +
                        "AND release_date='%s' " +
                        "AND duration_in_minutes=%d " +
                        "AND f.mpa_rating_id=%d",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId()), new FilmMapper());
        return result;
    }

    @Override
    public Film changeFilm(Film film) {
        checkChange(film);
        jdbcTemplate.update("UPDATE films " +
                        "SET name=?,description=?, release_date=?, duration_in_minutes=?, mpa_rating_id=? " +
                        "WHERE film_id=?",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        return showFilmById(film.getId());
    }

    @Override
    public void deleteFilmById(int id) {
        jdbcTemplate.update("DELETE " +
                "FROM films " +
                "WHERE film_id = ?", id);
    }

    @Override
    public Film showFilmById(int id) {
        try {
            Film film = jdbcTemplate.queryForObject(format(
                    "SELECT film_id, f.name as name, " +
                            "           description, " +
                            "           release_date, " +
                            "           duration_in_minutes, " +
                            "           f.mpa_rating_id as mpa_rating_id, " +
                            "           fm.name as mpa_name " +
                            "FROM films as f " +
                            "LEFT OUTER JOIN mpa_ratings as fm " +
                            "ON f.mpa_rating_id = fm.mpa_rating_id  " +
                            "WHERE f.film_id=%d ", id), new FilmMapper());
            return film;
        } catch (EmptyResultDataAccessException e) {
            log.warn("Не возможно найти film с id - {}.", id);
            throw new FilmNotFoundException(String.format("Cannot search film by %s.", id));
        }
    }

    @Override
    public List<Film> showFilms() {
        List<Film> films = jdbcTemplate.query(
                "SELECT film_id, f.name as name, description, release_date, " +
                        "duration_in_minutes,  f.mpa_rating_id as mpa_rating_id, fm.name as mpa_name " +
                        "FROM films as f LEFT OUTER JOIN mpa_ratings as fm ON f.mpa_rating_id = fm.mpa_rating_id  ",
                new FilmMapper());
        return films;
    }

    @Override
    public List<Integer> showUsersLikedFilms(int id) {
        return jdbcTemplate.queryForList(format("SELECT film_id FROM film_likes WHERE user_id = %d", id), Integer.class);
    }

    private void checkAdd(Film film) {
        if (film.getId() != null) {
            log.error("ID не должен быть указан при добавление!");
            throw new FilmIdNotNullException("ID не должен быть указан при добавление!");
        }

        LocalDate localDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(localDate)) {
            log.error("Ошибка добавление даты {}, дата должна быть после {}.", film.getReleaseDate(), localDate);
            throw new DateReleaseException("Date is wrong.");
        }
    }

    private void checkChange(Film film) {
        showFilmById(film.getId());

        LocalDate localDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(localDate)) {
            log.warn("Ошибка добавление даты {}, дата должна быть после {}.", film.getReleaseDate(), localDate);
            throw new DateReleaseException("Date is wrong.");
        }
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sql = "SELECT f.*, M.* " +
                "FROM FILM_LIKES " +
                "JOIN FILM_LIKES fl ON fl.FILM_ID = FILM_LIKES.FILM_ID " +
                "JOIN FILMS f on f.film_id = fl.film_id " +
                "JOIN MPA_RATINGS M on f.mpa_rating_id = M.MPA_RATING_ID " +
                "WHERE fl.USER_ID = ? AND FILM_LIKES.USER_ID = ?";
        return jdbcTemplate.query(sql, new FilmMapper(), userId, friendId);
    }
}
