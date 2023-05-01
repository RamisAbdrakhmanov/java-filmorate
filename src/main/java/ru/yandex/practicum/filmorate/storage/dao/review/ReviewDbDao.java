package ru.yandex.practicum.filmorate.storage.dao.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.notfound.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exeption.validate.ReviewValidateException;
import ru.yandex.practicum.filmorate.exeption.validate.UserIdNotNullException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.mapper.ReviewMapper;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewDbDao implements ReviewDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Review> getReviews(Integer filmId, Integer count) {
        List<Review> reviews;
        if (filmId != 0) {
            log.info("Запрос на получение списка отзывов на фильм с id = {} длиной = {}", filmId, count);
            reviews = jdbcTemplate.query(format("" +
                    "SELECT review_id, user_id, film_id, content, is_positive " +
                    "FROM reviews " +
                    "WHERE film_id=%d", filmId), new ReviewMapper());
        } else {
            log.info("Запрос на получение списка отзывов длиной = {}", count);
            reviews = jdbcTemplate.query("" +
                    "SELECT review_id, user_id, film_id, content, is_positive " +
                    "FROM reviews ", new ReviewMapper());
        }
        return reviews.stream().limit(count).collect(Collectors.toList());
    }

    @Override
    public Review getReview(Integer reviewId) {
        log.info("Запрос на получение отзыва с id - {}", reviewId);
        try {
            Review review = jdbcTemplate.queryForObject(format("" +
                    "SELECT review_id, user_id, film_id, content, is_positive " +
                    "FROM reviews " +
                    "WHERE review_id = %d", reviewId), new ReviewMapper());

            return review;
        } catch (EmptyResultDataAccessException e) {
            String message = String.format("Невозможно найти отзыв с id = %d", reviewId);
            log.warn(message);
            throw new ReviewNotFoundException(message);
        }
    }

    @Override
    public Review addReview(Review review) {
        log.info("Запрос на добавление отзыва с id = {}", review);
        checkAdd(review);

        jdbcTemplate.update("" +
                        "INSERT INTO reviews ( user_id, film_id, content, is_positive) " +
                        "VALUES ( ?,?,?,? )",
                review.getUserId(),
                review.getFilmId(),
                review.getContent(),
                review.getIsPositive()
        );

        Review getReview = jdbcTemplate.queryForObject(format("" +
                "SELECT review_id, user_id, film_id, content, is_positive " +
                "FROM reviews " +
                "WHERE user_id = %d AND film_id = %d ", review.getUserId(), review.getFilmId()), new ReviewMapper());

        return getReview;
    }

    @Override
    public Review updateReview(Review review) {
        log.info("запрос на изменение review - {}", review);
        getReview(review.getReviewId());
        jdbcTemplate.update("" +
                        "UPDATE reviews " +
                        "SET content=?, is_positive=? " +
                        "WHERE review_id=? ",
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        return getReview(review.getReviewId());
    }

    @Override
    public void deleteReview(Integer reviewId) {
        log.info("Запрос на удаление отзыва с id = {}", reviewId);
        jdbcTemplate.update("" +
                        "DELETE " +
                        "FROM reviews " +
                        "WHERE review_id = ?",
                reviewId);
    }

    /**
     * Проверка на добавления отзыва:
     * 1. Один пользователь может оставить только один отзыв к фильму.
     * 2. Пользователь должен существовать (проверить надо)
     */
    private void checkAdd(Review review) {
        log.info("Проверка на добавление review = {}", review);

        if (review.getReviewId() != null) {
            String message = "user_id не должно иметь значение";
            log.error(message);
            throw new UserIdNotNullException(message);
        }
        Integer check = jdbcTemplate.queryForObject(format("" +
                                "SELECT count(*) " +
                                "FROM reviews " +
                                "WHERE user_id=%d " +
                                "AND film_id=%d",
                        review.getUserId(),
                        review.getFilmId()),
                Integer.class);

        if (check != 0) {
            throw new ReviewValidateException("Ошибка валидация: пользователь уже оставлял отзыв к этому фильму.");
        }
    }
}
