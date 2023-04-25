package ru.yandex.practicum.filmorate.storage.dao.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.notfound.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.mapper.DirectorMapper;

import java.util.List;

@Component("DirectorDbDao")
@RequiredArgsConstructor
@Slf4j
public class DirectorDbDao implements DirectorDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director addDirector(Director director) {
        jdbcTemplate.update("INSERT INTO directors (name) VALUES (?)",
                director.getName());
        return jdbcTemplate.queryForObject(
                "SELECT * FROM directors WHERE director_id = " +
                        "(SELECT MAX(director_id) FROM directors)",
                new DirectorMapper());
    }

    public Director changeDirector(Director director) {
        jdbcTemplate.update("UPDATE directors SET mame = ? WHERE director_id = ? ",
                director.getName(),
                director.getId());
        return getDirector(director.getId());
    }

    public Director getDirector(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM directors WHERE director_id = ?",
                    new DirectorMapper(),
                    id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Невозможно найти режиссёра с id - {}", id);
            throw new DirectorNotFoundException(String.format("Cannot search director by %s.", id));
        }
    }

    public List<Director> getDirectors() {
        return jdbcTemplate.query("SELECT * FROM directors",
                new DirectorMapper());
    }
}
