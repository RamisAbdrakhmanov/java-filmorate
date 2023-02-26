package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Set<Film> films = new HashSet<>();
    public static int idCount = 1;

    @GetMapping
    public Set<Film> showFilms() {
     log.info("Список films содержит - {} элементов", films.size());
        return films;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
       log.info("Запросто на добавление элемента.");
       LocalDate localDate = LocalDate.of(1895, 12, 28);

        if (film.getReleaseDate().isBefore(localDate)) {
            log.warn("Ошибка добавление даты {}, дата должна быть поле {}.",film.getReleaseDate(),localDate);
            throw new CustomValidationException("Date is wrong.");
        }
        if (film.getId() == 0) {
            film.setId(idCount);
            idCount++;
        }
        films.add(film);
        return film;
    }

    @PutMapping
    public Film changeFilm(@Valid @RequestBody Film film) {
        log.info("Запросто на изменение элемента с id = {} .",film.getId());

        for (Film filmSet : films) {
            if (filmSet.getName().equals(film.getName())) {
                log.warn("Имя {} уже существует в базе. Элемент в таким же именем {}",film.getName(),filmSet);
                throw new CustomValidationException("There is already a movie with this name.");
            }
        }
        films.remove(film);
        films.add(film);
        return film;
    }

    public Set<Film> getFilms() {
        return films;
    }
}
