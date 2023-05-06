package ru.yandex.practicum.filmorate.storage.dao.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.notfound.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exeption.notfound.UserNotFoundException;
import ru.yandex.practicum.filmorate.storage.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.util.SlopeOne;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Integer[]> films = jdbcTemplate.query("SELECT * FROM film_likes", new RatingMapper());
        HashMap<Integer, Map<Integer, Double>> userFilms = new HashMap<>();
        for (Integer[] oneString : films) {
                if (userFilms.containsKey(oneString[0])) {
                    userFilms.get(oneString[0]).put(oneString[1], oneString[2].doubleValue());
                } else {
                    userFilms.put(oneString[0], Map.of(oneString[1], oneString[2].doubleValue()));
                }
        }
        SlopeOne slopeOne = new SlopeOne();
        slopeOne.buildDifferencesMatrix(userFilms);
        Map<Integer, Map<Integer, Integer>> usersPrediction = slopeOne.predict(userFilms);
        List<Integer> listOfFilms = new ArrayList<>();
        try {
            Map<Integer, Integer> predictedFilms = usersPrediction.get(userID);
            for (Integer film : predictedFilms.keySet()) {
                if (predictedFilms.get(film) > 5) {
                    listOfFilms.add(film);
                }
            }
        } catch (NullPointerException e) {
            throw new UserNotFoundException(String.format("Пользователь с id %s не найден", userID));
        }
        return listOfFilms;
    }
}
