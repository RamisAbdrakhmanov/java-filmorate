package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RatingMapper implements RowMapper<Integer[]> {

    @Override
    public Integer[] mapRow(ResultSet rs, int mapRow) throws SQLException {
        Integer[] rating = new Integer[3];
        rating[0] = rs.getInt(1);
        rating[1] = rs.getInt(2);
        rating[2] = rs.getInt(3);
        return rating;
    }
}
