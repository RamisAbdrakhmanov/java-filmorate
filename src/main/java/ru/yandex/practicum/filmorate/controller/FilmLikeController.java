package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmLikeController {

    private final FilmService filmService;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmLikeController(FilmService filmService, FilmStorage filmStorage, UserStorage userStorage) {
        this.filmService = filmService;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @GetMapping("/popular")
    public List<Film> showPopularFilms(@RequestParam(defaultValue = "10", required = false) int count) {
        return filmService.showPopularFilms(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable(name = "id") int filmId, @PathVariable int userId) {
        User user = userStorage.showUserById(userId);
        Film film = filmStorage.showFilmById(filmId);

        filmService.addLike(film, user);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable(name = "id") int filmId, @PathVariable int userId) {
        User user = userStorage.showUserById(userId);
        Film film = filmStorage.showFilmById(filmId);

        filmService.deleteLike(film, user);
    }

}
