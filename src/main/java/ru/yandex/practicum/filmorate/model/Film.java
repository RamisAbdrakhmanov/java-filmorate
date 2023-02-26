package ru.yandex.practicum.filmorate.model;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Film film = (Film) o;

        return id == film.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

   /*
    дата релиза — не раньше 28 декабря 1895 года;
    */
}
