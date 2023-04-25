package ru.yandex.practicum.filmorate.storage.dao.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorDao {
    public Director addDirector(Director director);

    public Director changeDirector(Director director);

    public Director getDirector(int id);

    public List<Director> getDirectors();
}
