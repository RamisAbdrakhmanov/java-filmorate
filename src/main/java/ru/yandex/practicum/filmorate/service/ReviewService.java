package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.review.ReviewDao;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDao reviewDao;

    public List<Review> getReviews() {
        return reviewDao.getReviews();
    }

    public Review getReview(int reviewId) {
        return reviewDao.getReview(reviewId);
    }

    public Review addReview(Review review) {
        return reviewDao.addReview(review);
    }

    public Review changeReview(Review review) {
        return reviewDao.changeReview(review);
    }

    public void deleteReview(int reviewId) {

    }
}
