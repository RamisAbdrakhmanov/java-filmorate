package ru.yandex.practicum.filmorate.storage.dao.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.notfound.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.MpaMapper;

import java.util.List;

import static java.lang.String.format;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MpaDbDao implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa getById(Integer id) {
        log.info("Запрос на получение рейтинга с id = {}", id);
        try {
            Mpa mpa = jdbcTemplate.queryForObject(format(""
                    + "SELECT mpa_rating_id, name "
                    + "FROM mpa_ratings "
                    + "WHERE mpa_rating_id=%s", id), new MpaMapper());
            return mpa;
        } catch (EmptyResultDataAccessException e) {
            log.error("Не возможно найти пользователя с id - {}.", id);
            throw new MpaNotFoundException(format("Не возможно найти пользователя с id - %s.", id));
        }
    }


    @Override
    public List<Mpa> getAll() {
        log.info("Запрос на получение списка всех пользователей");
        List<Mpa> result = jdbcTemplate.query(""
                + "SELECT mpa_rating_id, name "
                + "FROM mpa_ratings "
                + "ORDER BY mpa_rating_id", new MpaMapper());

        return result;
    }

}
