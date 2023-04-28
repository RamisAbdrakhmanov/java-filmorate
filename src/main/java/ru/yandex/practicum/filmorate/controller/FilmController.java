package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public Film showFilmById(@PathVariable int id) {
        return filmService.showFilmById(id);
    }

    @GetMapping
    public List<Film> showFilms() {
        return filmService.showFilms();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Запросто на добавление элемента.");
        return filmService.addFilm(film);

    }

    @PutMapping
    public Film changeFilm(@Valid @RequestBody Film film) {
        log.info("Запрос на изменение элемента с id = {} .", film.getId());
        return filmService.changeFilm(film);
    }

    @DeleteMapping("/{id}")
    public void deleteFilmById(@PathVariable int id) {
        log.info("Запрос на удаление элемента с id = {} .", id);
        filmService.deleteFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> showPopularFilms(@RequestParam(defaultValue = "10", required = false) int count,
                                       @RequestParam(required = false) Integer genreId,
                                       @RequestParam(required = false) Integer year) {
        return filmService.showPopularFilms(count, genreId, year);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam(required = false) String query,
                                  @RequestParam(required = false) String by) {
        return filmService.searchFilms(query, by);
    }


    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable(name = "id") int filmId, @PathVariable int userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable(name = "id") int filmId, @PathVariable int userId) {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilms(@PathVariable(name = "directorId") int directorId, @RequestParam String sortBy) {
        return filmService.getDirectorFilms(directorId, sortBy);
    }

}
