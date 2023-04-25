package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review implements Comparable<Review> {

    Integer reviewId;
    @NotNull(message = "Film name cannot be not null")
    @NotEmpty(message = "Film name cannot be not empty")
    @NotBlank(message = "Film name cannot be not blank")
    String content;
    Boolean isPositive;
    @NotNull
    Integer userId;
    @NotNull
    Integer filmId;
    Integer useful;


    @Override
    public int compareTo(Review o) {
        return this.getUseful() - o.getUseful();
    }
}
