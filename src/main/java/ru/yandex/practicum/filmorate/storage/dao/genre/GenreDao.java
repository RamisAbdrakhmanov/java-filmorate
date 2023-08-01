package ru.yandex.practicum.filmorate.storage.dao.genre;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

@Component
public interface GenreDao {
    Genre getGenreById(Integer id);

    List<Genre> getGenres();

    void addGenres(Integer id, Set<Genre> genres);

    void updateGenres(Integer id, Set<Genre> genres);

    void deleteGenre(Integer id);

    Set<Genre> getGenres(Integer id);
}
