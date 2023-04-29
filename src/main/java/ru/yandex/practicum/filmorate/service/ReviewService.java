package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmDao;
import ru.yandex.practicum.filmorate.storage.dao.review.ReviewDao;
import ru.yandex.practicum.filmorate.storage.dao.review.like.ReviewLikeDao;
import ru.yandex.practicum.filmorate.storage.dao.user.UserDao;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDao reviewDao;
    private final ReviewLikeDao reviewLikeDao;
    private final FilmDao filmDao;
    private final UserDao userDao;

    public List<Review> getReviews(int filmId, int count) {
        if (filmId != 0) {
            filmDao.showFilmById(filmId);
        }
        return reviewDao.getReviews(filmId, count).stream().map(this::addLikes).sorted().collect(Collectors.toList());
    }

    public Review getReview(int reviewId) {
        return addLikes(reviewDao.getReview(reviewId));
    }

    public Review addReview(Review review) {
        checkFilmAndUserId(review);
        return addLikes(reviewDao.addReview(review));
    }

    public Review updateReview(Review review) {
        return addLikes(reviewDao.updateReview(review));
    }

    public void deleteReview(int reviewId) {
        reviewLikeDao.deleteAll(reviewId);
        reviewDao.deleteReview(reviewId);
    }

    public void addLike(int reviewId, int userId) {
        checkReviewAndUserId(reviewId, userId);
        reviewLikeDao.addLike(reviewId, userId);
    }

    public void addDislike(int reviewId, int userId) {
        checkReviewAndUserId(reviewId, userId);
        reviewLikeDao.addDislike(reviewId, userId);
    }

    public void deleteLike(int reviewId, int userId) {
        checkReviewAndUserId(reviewId, userId);
        reviewLikeDao.deleteLike(reviewId, userId);
    }

    public void deleteDislike(int reviewId, int userId) {
        checkReviewAndUserId(reviewId, userId);
        reviewLikeDao.deleteDislike(reviewId, userId);
    }

    private Review addLikes(Review review) {

        review.setUseful(reviewLikeDao.getCountLikes(review.getReviewId()));
        return review;
    }

    private void checkReviewAndUserId(int reviewId, int userId) {
        reviewDao.getReview(reviewId);
        userDao.showUserById(userId);
    }

    private void checkFilmAndUserId(Review review) {
        filmDao.showFilmById(review.getFilmId());
        userDao.showUserById(review.getUserId());
    }
}
