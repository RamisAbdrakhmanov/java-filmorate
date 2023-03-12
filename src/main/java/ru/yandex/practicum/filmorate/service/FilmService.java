package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(Film film, User user) {
        film.addLike(user);
    }

    public void deleteLike(Film film, User user) {
        film.deleteLike(user);
    }

    public List<Film> showPopularFilms(int count) {
        return filmStorage.showFilms().stream()
                .sorted((i1,i2)->(i2.getAmountLikes().compareTo(i1.getAmountLikes())))
                .limit(count)
                .collect(Collectors.toList());
    }
}
