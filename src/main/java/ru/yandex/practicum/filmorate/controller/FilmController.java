package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;

    public FilmController(FilmStorage filmStorage) {

        this.filmStorage = filmStorage;
    }

    @GetMapping("/{id}")
    public Film showFilmById(@PathVariable int id) {
        return filmStorage.showFilmById(id);
    }

    @GetMapping
    public List<Film> showFilms() {
        return filmStorage.showFilms();
    }


    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Запросто на добавление элемента.");
        return filmStorage.addFilm(film);

    }

    @PutMapping
    public Film changeFilm(@Valid @RequestBody Film film) {
        log.info("Запрос на изменение элемента с id = {} .", film.getId());
        return filmStorage.changeFilm(film);
    }

    @DeleteMapping("/{id}")
    public void deleteFilmById(@PathVariable int id) {
        log.info("Запрос на удаление элемента с id = {} .", id);
        filmStorage.deleteFilmById(id);
    }

}
