package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review implements Comparable<Review> {

    private Integer reviewId;

    @NotEmpty(message = "Film name cannot be not empty")
    @NotBlank(message = "Film name cannot be not blank")
    private String content;
    private Boolean isPositive;
    @NotNull
    private Integer userId;
    @NotNull
    private Integer filmId;
    private Integer useful;

    @Override
    public int compareTo(Review o) {
        return o.getUseful() - this.getUseful();
    }
}
