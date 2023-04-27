package ru.yandex.practicum.filmorate.storage.dao.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface DirectorDao {
    public Director addDirector(Director director);

    public void addFilmDirectors(int filmId, Set<Director> directors);

    public void updateFilmDirectors(int filmId, Set<Director> directors);

    public Director changeDirector(Director director);

    public Director getDirector(int id);

    public List<Director> getDirectors();

    public Set<Director> getFilmDirectors(int filmId);

    public List<Film> getDirectorFilms(int directorId, String sortBy);

    public void deleteDirector(int id);
}
