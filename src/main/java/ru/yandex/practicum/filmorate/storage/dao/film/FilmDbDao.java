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
import ru.yandex.practicum.filmorate.exeption.validate.FilmNameAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Mpa;


import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

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

        try {
            Film nameFilm = (jdbcTemplate.queryForObject(format(
                    "SELECT film_id, f.name as name, description, release_date, " +
                            "           duration_in_minutes, f.mpa_rating_id as mpa_rating_id, fm.name as mpa_name " +
                            "FROM films as f " +
                            "LEFT OUTER JOIN mpa_ratings as fm " +
                            "ON f.mpa_rating_id = fm.mpa_rating_id  " +
                            "WHERE f.name= '%s' ", film.getName()), new FilmMapper()));

            log.error("Фильм в именем - {} уже имеет ID - {}",
                    Objects.requireNonNull(nameFilm).getName(),
                    nameFilm.getId());
            throw new FilmNameAlreadyExistException(
                    String.format("Фильм в именем - %s уже имеет ID - %s",
                            nameFilm.getName(),
                            nameFilm.getId()));

        } catch (EmptyResultDataAccessException e) { // эта ошибка обрабатывает NULL на выходе из базы,
            // иначе не нашел как мне получить или null или значение.
            log.info("Фильм с именем {} отсутствует!", film.getName());
        }


    }

    private void checkChange(Film film) {
        showFilmById(film.getId());

        LocalDate localDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(localDate)) {
            log.warn("Ошибка добавление даты {}, дата должна быть после {}.", film.getReleaseDate(), localDate);
            throw new DateReleaseException("Date is wrong.");
        }

        try {
            Film nameFilm = jdbcTemplate.queryForObject(format(
                    "SELECT film_id, f.name as name, description, release_date, " +
                            "           duration_in_minutes, f.mpa_rating_id as mpa_rating_id, fm.name as mpa_name " +
                            "FROM films as f " +
                            "LEFT OUTER JOIN mpa_ratings as fm " +
                            "ON f.mpa_rating_id = fm.mpa_rating_id  " +
                            "WHERE f.name= '%s' ", film.getName()), new FilmMapper());

            if (nameFilm.getId() != film.getId()) {
                log.error("Фильм в именем - {} уже имеет ID - {}", nameFilm.getName(), nameFilm.getId());
                throw new FilmNameAlreadyExistException(
                        String.format("Фильм в именем - %s уже имеет ID - %s", nameFilm.getName(), nameFilm.getId()));
            }
        } catch (EmptyResultDataAccessException e) {
            log.info("Фильм с именем {} отсутствует!", film.getName());
        }
    }

    static Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        long duration = rs.getLong("duration_in_minutes");
        Mpa mpa = new Mpa(rs.getInt("mpa_ratings.mpa_rating_id"), rs.getString("mpa_ratings.name"));
        return new Film(id, name, description, releaseDate, duration, mpa, new HashSet<>());
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sql = "SELECT f.*, M.* " +
                "FROM FILM_LIKES " +
                "JOIN FILM_LIKES fl ON fl.FILM_ID = FILM_LIKES.FILM_ID " +
                "JOIN FILMS f on f.film_id = fl.film_id " +
                "JOIN MPA_RATINGS M on f.mpa_rating_id = M.MPA_RATING_ID " +
                "WHERE fl.USER_ID = ? AND FILM_LIKES.USER_ID = ?";
        return jdbcTemplate.query(sql, FilmDbDao::makeFilm, userId, friendId);
    }
}
