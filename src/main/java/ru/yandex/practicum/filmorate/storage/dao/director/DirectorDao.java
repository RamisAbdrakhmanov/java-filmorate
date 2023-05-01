package ru.yandex.practicum.filmorate.storage.dao.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface DirectorDao {
    public Director addDirector(Director director);

    public void addFilmDirectors(Integer filmId, Set<Director> directors);

    public void updateFilmDirectors(Integer filmId, Set<Director> directors);

    public Director updateDirector(Director director);

    public Director getDirector(Integer id);

    public List<Director> getDirectors();

    public Set<Director> getFilmDirectors(Integer filmId);

    public List<Film> getDirectorFilms(Integer directorId, String sortBy);

    public void deleteDirector(Integer id);
}
