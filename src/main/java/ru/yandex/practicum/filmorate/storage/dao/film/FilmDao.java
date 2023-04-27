package ru.yandex.practicum.filmorate.storage.dao.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
public interface FilmDao {

    Film showFilmById(int id);

    List<Film> showFilms();

    Film addFilm(Film film);

    Film changeFilm(Film film);

    List<Integer> showUsersLikedFilms(int id);

    void deleteFilmById(int id);

    List<Film> getCommonFilms(int userId, int friendId);

}
