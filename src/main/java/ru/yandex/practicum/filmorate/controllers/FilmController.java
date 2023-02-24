package ru.yandex.practicum.filmorate.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    public Set<Film> films = new HashSet<>();

    @GetMapping
    public Set<Film> getFilms(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return films;
    }

    @PostMapping
    public void addFilm(@Valid @RequestBody Film film, HttpServletRequest request) {
        try {

            log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                    request.getMethod(), request.getRequestURI(), request.getQueryString());
            LocalDate localDate = LocalDate.of(1895, 12, 28);

            if (film.getReleaseDate().isBefore(localDate)) {
                throw new ValidationException("Wrong release date.");
            }
            films.add(film);
        } catch (ValidationException v) {
            System.out.println(v.getMessage());
        }
    }

    @PutMapping
    public void changeFilm(@Valid @RequestBody Film film, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());

        films.add(film);
    }
}
