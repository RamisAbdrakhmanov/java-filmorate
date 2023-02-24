package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

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
