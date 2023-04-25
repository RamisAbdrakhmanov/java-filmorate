package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@ResponseStatus(HttpStatus.OK)
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam(defaultValue = "0", required = false) int filmId,
                                   @RequestParam(defaultValue = "10", required = false) int count) {
        return null;
    }

    @GetMapping("/{reviewId}")
    public Review getReview(@PathVariable int reviewId){
        return reviewService.getReview(reviewId);
    }

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review){

        return reviewService.addReview(review);
    }

    @PutMapping
    public Review changeReview(@Valid @RequestBody Review review){
        return reviewService.changeReview(review);
    }
    @DeleteMapping("/{reviewId}")
    public void deleteReview(@PathVariable int reviewId){
        reviewService.deleteReview(reviewId);
    }

}
