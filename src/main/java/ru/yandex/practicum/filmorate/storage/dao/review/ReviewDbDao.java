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
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewDbDao implements ReviewDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Review> getReviews(int filmId, int count) {
        List<Review> reviews;
        if (filmId != 0) {
            reviews = jdbcTemplate.query(format("" +
                    "SELECT review_id, user_id, film_id, content, is_positive " +
                    "FROM reviews " +
                    "WHERE film_id=%d", filmId), new ReviewMapper());
        } else {
            reviews = jdbcTemplate.query("" +
                    "SELECT review_id, user_id, film_id, content, is_positive " +
                    "FROM reviews ", new ReviewMapper());
        }
        return reviews.stream().limit(count).collect(Collectors.toList());
    }

    @Override
    public Review getReview(int reviewId) {
        log.info("Показать review по id - {}", reviewId);
        try {
            Review review = jdbcTemplate.queryForObject(format("" +
                    "SELECT review_id, user_id, film_id, content, is_positive " +
                    "FROM reviews " +
                    "WHERE review_id = %d", reviewId), new ReviewMapper());

            return review;
        } catch (EmptyResultDataAccessException e) {
            log.warn("Не возможно найти review с id - {}.", reviewId);
            throw new ReviewNotFoundException(String.format("Не возможно найти review с id - %d.", reviewId));
        }
    }

    @Override
    public Review addReview(Review review) {
        log.info("запрос на добавление review - {}", review);
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
    public Review changeReview(Review review) {
        log.info("запрос на изменение review - {}", review);
        checkChange(review);
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
    public void deleteReview(int reviewId) {
        log.info("Удаление review");
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
        log.info("проверка на добавление review - {}", review);

        if (review.getReviewId() != null) {
            log.error("user_id не должно иметь значение");
            throw new UserIdNotNullException("user_id не должно иметь значение");
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

    /**
     * Проверка на добавления отзыва:
     * 1. Один пользователь может оставить только один отзыв к фильму.
     * 2. Отзыв существовал прежде
     * 3. User и Film отзыва, не могут быть изменены;
     */
    private void checkChange(Review review) {
        log.info("проверка на добавление review - {}", review);
        Review changeReview = getReview(review.getReviewId());

        if (!Objects.equals(changeReview.getUserId(), review.getUserId()) ||
                !Objects.equals(changeReview.getFilmId(), review.getFilmId())) {
            throw new ReviewValidateException("User и Film отзыва, не могут быть изменены");
        }
    }
}
