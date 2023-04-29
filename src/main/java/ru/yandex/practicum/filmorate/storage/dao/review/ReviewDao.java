package ru.yandex.practicum.filmorate.storage.dao.review;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Component
public interface ReviewDao {
    List<Review> getReviews(int filmId, int count);

    Review getReview(int reviewId);

    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReview(int reviewId);


}
