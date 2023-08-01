package ru.yandex.practicum.filmorate.storage.dao.review.like;

import org.springframework.stereotype.Component;

@Component
public interface ReviewLikeDao {
    void addLike(int reviewId, int userId);

    void addDislike(int reviewId, int userId);

    void deleteLike(int reviewId, int userId);

    void deleteDislike(int reviewId, int userId);

    Integer getCountLikes(int reviewId);

    void deleteAll(int reviewId);
}
