package ru.yandex.practicum.filmorate.storage.dao.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
public interface FilmDao {

    Film getFilmById(Integer id);

    List<Film> getFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Integer> getUsersLikedFilms(Integer id);

    void deleteFilmById(Integer id);

    List<Film> getCommonFilms(Integer userId, Integer friendId);

    List<Film> searchFilms(String query, String by);

    List<Film> getBatchFilmsByIds(List<Integer> filmIds);
}
