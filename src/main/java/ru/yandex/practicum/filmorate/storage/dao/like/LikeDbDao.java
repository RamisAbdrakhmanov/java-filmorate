package ru.yandex.practicum.filmorate.storage.dao.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.notfound.LikeNotFoundException;

import java.util.List;

import static java.lang.String.format;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LikeDbDao implements LikeDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Integer filmID, Integer userID) {
        try {
            log.info("Запрос на добавление like фильму с id - {} от user c id - {}", filmID, userID);
            jdbcTemplate.update(""
                    + "INSERT INTO film_likes (film_id, user_id) "
                    + "VALUES (?, ?)", filmID, userID);
        } catch (EmptyResultDataAccessException e) {
            log.error("Не возможно найти film с id - {}.(ну или user(-_-))", filmID);
            throw new LikeNotFoundException(format("Не возможно найти film с id - %d.(ну или user(-_-))", filmID));
        }
    }

    @Override
    public void deleteLike(Integer filmID, Integer userID) {
        log.info("Запрос на лайка like у фильма с id - {} от user c id - {}", filmID, userID);
        try {
            jdbcTemplate.update(""
                    + "DELETE FROM film_likes "
                    + "WHERE film_id=? "
                    + "AND user_id=?", filmID, userID);
        } catch (EmptyResultDataAccessException e) {
            log.error("Не возможно найти film с id - {}.(ну или user(-_-))", filmID);
            throw new LikeNotFoundException(format("Не возможно найти film с id - %d.(ну или user(-_-))", filmID));
        }
    }

    @Override
    public List<Integer> getLikesSort(Integer count) {
        log.info("Запрос на вывод самых популярных фильмов в количестве - {}", count);
        List<Integer> filmId = jdbcTemplate.queryForList(format(""
                + "SELECT film_id "
                + "FROM film_likes "
                + "GROUP BY  film_id "
                + "ORDER BY COUNT(DISTINCT user_id) "
                + "LIMIT %d", count), Integer.class);
        return filmId;
    }

    @Override
    public List<Integer> getRecommendedList(Integer userID) {
        log.info("Запрос на вывод списка рекомендованных фильмов для пользователя с id = {}", userID);
        return jdbcTemplate.queryForList(format("SELECT fl2.FILM_ID " +
                "FROM film_likes fl2 " +
                "WHERE fl2.user_id = " +
                "(SELECT fl1.user_id " +
                "FROM film_likes fl1 " +
                "WHERE fl1.user_id <> %d AND fl1.film_id IN " +
                "(SELECT fl.film_id " +
                "FROM film_likes fl " +
                "WHERE user_id = %d) " +
                "GROUP BY fl1.user_id " +
                "ORDER BY COUNT(fl1.film_id) DESC " +
                "LIMIT 1);", userID, userID), Integer.class);
    }
}
