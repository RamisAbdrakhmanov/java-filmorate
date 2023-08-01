package ru.yandex.practicum.filmorate.storage.dao.review.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import static java.lang.String.format;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewLikeDbDao implements ReviewLikeDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Integer getCountLikes(int reviewId) {
        Integer countTrue = jdbcTemplate.queryForObject(format("" +
                "SELECT COUNT(*) " +
                "FROM review_likes " +
                "WHERE review_id=%d " +
                "AND is_like=true", reviewId
        ), Integer.class);
        Integer countFalse = jdbcTemplate.queryForObject(format("" +
                "SELECT COUNT(*) " +
                "FROM review_likes " +
                "WHERE review_id=%d " +
                "AND is_like=false", reviewId
        ), Integer.class);
        return countTrue - countFalse;
    }

    @Override
    public void addLike(int reviewId, int userId) {
        add(reviewId, userId, true);
    }

    @Override
    public void addDislike(int reviewId, int userId) {
        add(reviewId, userId, false);
    }

    @Override
    public void deleteLike(int reviewId, int userId) {
        delete(reviewId, userId);
    }

    @Override
    public void deleteDislike(int reviewId, int userId) {
        delete(reviewId, userId);
    }

    @Override
    public void deleteAll(int reviewId) {
        jdbcTemplate.update("" +
                "DELETE FROM review_likes " +
                "WHERE review_id=? ", reviewId);
    }

    private void add(int reviewId, int userId, boolean status) {
        try {
            Boolean statusBefore = jdbcTemplate.queryForObject(format("" +
                            "SELECT is_like " +
                            "FROM review_likes " +
                            "WHERE review_id=%d " +
                            "AND user_id=%d",
                    reviewId,
                    userId), Boolean.class);
            if (statusBefore.equals(status)) {
                jdbcTemplate.update("" +
                                "UPDATE review_likes " +
                                "SET is_like=? " +
                                "WHERE review_id=? " +
                                "AND user_id=?",
                        status,
                        reviewId,
                        userId);
            }

        } catch (EmptyResultDataAccessException e) {
            jdbcTemplate.update("" +
                            "INSERT INTO review_likes (review_id,user_id,is_like) " +
                            "VALUES (?,?,?)",
                    reviewId,
                    userId,
                    status);
        }
    }

    private void delete(int reviewId, int userId) {
        jdbcTemplate.update("" +
                        "DELETE FROM review_likes " +
                        "WHERE review_id=? " +
                        "AND user_id=?",
                reviewId,
                userId);
    }


}
