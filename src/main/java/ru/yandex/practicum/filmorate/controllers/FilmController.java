package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    public Set<Film> films = new HashSet<>();
    public static int idCount = 1;

    @GetMapping
    public Set<Film> getFilms(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return films;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        LocalDate localDate = LocalDate.of(1895, 12, 28);

        if (film.getReleaseDate().isBefore(localDate)) {
            throw new ValidationException();
        }
        if (film.getId() == 0) {
            film.setId(idCount);
            idCount++;
        }
        films.add(film);
        return film;
    }

    @PutMapping
    public Film changeFilm(@Valid @RequestBody Film film, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());

        for (Film filmSet : films) {
            if (filmSet.getName().equals(film.getName())) {
                throw new ValidationException("Email is using");
            }
        }
        films.remove(film);
        films.add(film);
        return film;
    }
}
