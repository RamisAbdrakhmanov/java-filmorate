package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.notfound.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exeption.notfound.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exeption.notfound.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.director.DirectorDao;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmDao;
import ru.yandex.practicum.filmorate.storage.dao.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.like.LikeDao;
import ru.yandex.practicum.filmorate.storage.dao.mpa.MpaDao;
import ru.yandex.practicum.filmorate.storage.dao.user.UserDao;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmDao filmDao;
    private final UserDao userDao;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;
    private final LikeDao likeDao;
    private final DirectorDao directorDao;

    @Autowired
    public FilmService(FilmDao filmDao, UserDao userDao, GenreDao genreDao, MpaDao mpaDao, LikeDao likeDao, DirectorDao directorDao) {
        this.filmDao = filmDao;
        this.userDao = userDao;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
        this.likeDao = likeDao;
        this.directorDao = directorDao;
    }

    public void addLike(Integer filmId, Integer userId) {
        filmDao.getFilmById(filmId);
        userDao.getUserById(userId);
        likeDao.addLike(filmId, userId);
        userDao.addEvent(makeEvent("ADD", filmId, userId));
    }

    public void deleteLike(Integer filmId, Integer userId) {
        filmDao.getFilmById(filmId);
        userDao.getUserById(userId);
        likeDao.deleteLike(filmId, userId);
        userDao.addEvent(makeEvent("REMOVE", filmId, userId));
    }

    public List<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        List<Integer> filmIds = likeDao.getLikesSort(count);
        Set<Film> films = new LinkedHashSet<>();
        films.addAll(filmDao.getBatchFilmsByIds(filmIds));

        if (films.isEmpty()) {
            films.addAll(getFilms());
        }

        List<Film> filteredFilms = new ArrayList<>(films);

        if (year != null) {
            filteredFilms.removeIf(f -> f.getReleaseDate().getYear() != year);
        }

        if (genreId != null) {
            Genre genre = genreDao.getGenreById(genreId);
            if (genre == null) {
                String message = String.format("Фильм с id = %d не найден", genreId);
                log.warn(message);
                throw new GenreNotFoundException(message);
            } else {
                filteredFilms.removeIf(f -> !f.getGenres().contains(genre));
            }
        }
        return filteredFilms.stream().peek(this::collectorFilm).limit(count).collect(Collectors.toList());
    }

    public List<Film> searchFilms(String query, String by) {
        return filmDao.searchFilms(query, by).stream()
                .peek(this::collectorFilm)
                .collect(Collectors.toList());
    }

    public Film getFilmById(Integer id) {
        Film film = filmDao.getFilmById(id);
        collectorFilm(film);
        return film;
    }

    public List<Film> getFilms() {
        return filmDao.getFilms().stream()
                .peek(this::collectorFilm)
                .collect(Collectors.toList());
    }

    public Film addFilm(Film film) {
        Film filmGenre = filmDao.addFilm(film);
        genreDao.addGenres(filmGenre.getId(), film.getGenres());
        directorDao.addFilmDirectors(filmGenre.getId(), film.getDirectors());
        collectorFilm(filmGenre);
        return filmGenre;
    }

    public Film updateFilm(Film film) {
        Film filmGenre = filmDao.updateFilm(film);
        genreDao.updateGenres(filmGenre.getId(), film.getGenres());
        directorDao.updateFilmDirectors(filmGenre.getId(), film.getDirectors());
        collectorFilm(filmGenre);
        return filmGenre;
    }

    public void deleteFilmById(int id) {
        filmDao.deleteFilmById(id);
    }

    public List<Film> getDirectorFilms(int directorId, String sorBy) {
        return directorDao.getDirectorFilms(directorId, sorBy).stream()
                .peek(this::collectorFilm)
                .collect(Collectors.toList());
    }

    private void collectorFilm(Film film) {
        film.setGenres(genreDao.getGenres(film.getId()));
        film.setDirectors(directorDao.getFilmDirectors(film.getId()));
    }

    public List<Film> getCommonFilms(int userId, int friendId) throws UserNotFoundException, MpaNotFoundException {
        return filmDao.getCommonFilms(userId, friendId);
    }

    private Event makeEvent(String operation, int filmId, int userId) {
        return Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(userId)
                .eventType("LIKE")
                .operation(operation)
                .eventId(0)
                .entityId(filmId)
                .build();
    }
}
