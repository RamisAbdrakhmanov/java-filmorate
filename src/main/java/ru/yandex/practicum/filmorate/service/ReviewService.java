package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
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


    public List<Review> getReviews(Integer filmId, Integer count) {
        if (filmId != 0) {
            filmDao.getFilmById(filmId);
        }
        return reviewDao.getReviews(filmId, count).stream().map(this::addLikes).sorted().collect(Collectors.toList());
    }

    public Review getReview(Integer reviewId) {
        return addLikes(reviewDao.getReview(reviewId));
    }

    public Review addReview(Review review) {
        checkFilmAndUserId(review);
        Review reviewAdd = addLikes(reviewDao.addReview(review));
        userDao.addEvent(makeEvent("ADD", reviewAdd));
        return reviewAdd;
    }

    public Review updateReview(Review review) {
        Review reviewUpdate = addLikes(reviewDao.updateReview(review));
        userDao.addEvent(makeEvent("UPDATE", reviewUpdate));
        return reviewUpdate;
    }

    public void deleteReview(Integer reviewId) {
        userDao.addEvent(makeEvent("REMOVE", getReview(reviewId)));
        reviewLikeDao.deleteAll(reviewId);
        reviewDao.deleteReview(reviewId);
    }

    public void addLike(Integer reviewId, Integer userId) {
        checkReviewAndUserId(reviewId, userId);
        reviewLikeDao.addLike(reviewId, userId);
    }

    public void addDislike(Integer reviewId, Integer userId) {
        checkReviewAndUserId(reviewId, userId);
        reviewLikeDao.addDislike(reviewId, userId);
    }

    public void deleteLike(Integer reviewId, Integer userId) {
        checkReviewAndUserId(reviewId, userId);
        reviewLikeDao.deleteLike(reviewId, userId);
    }

    public void deleteDislike(Integer reviewId, Integer userId) {
        checkReviewAndUserId(reviewId, userId);
        reviewLikeDao.deleteDislike(reviewId, userId);
    }

    private Review addLikes(Review review) {

        review.setUseful(reviewLikeDao.getCountLikes(review.getReviewId()));
        return review;
    }

    private void checkReviewAndUserId(Integer reviewId, Integer userId) {
        reviewDao.getReview(reviewId);
        userDao.getUserById(userId);
    }

    private void checkFilmAndUserId(Review review) {
        filmDao.getFilmById(review.getFilmId());
        userDao.getUserById(review.getUserId());
    }

    private Event makeEvent(String operation, Review review) {
        return Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(review.getUserId())
                .eventType("REVIEW")
                .operation(operation)
                .eventId(0)
                .entityId(review.getReviewId())
                .build();
    }
}
