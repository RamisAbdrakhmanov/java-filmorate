package ru.yandex.practicum.filmorate.storage.dao.review;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Component
public interface ReviewDao {
    List<Review> getReviews(Integer filmId, Integer count);

    Review getReview(Integer reviewId);

    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReview(Integer reviewId);


}
