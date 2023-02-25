package ru.yandex.practicum.filmorate.model;


import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @NotEmpty(message = "Film name cannot be not empty")
    private String name;
    @NotEmpty
    @Size(max = 200, message = "Film description must be less than 200 characters.")
    private String description;

    private LocalDate releaseDate;
    @Min(value = 0L, message = "Film duration must be more that 0")
    private long duration;

   /*
    дата релиза — не раньше 28 декабря 1895 года;
    */
}
