package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        long timestamp = rs.getLong("timestamp");
        int userId = rs.getInt("user_id");
        String eventType = rs.getString("event_type");
        String operation = rs.getString("operation");
        int eventId = rs.getInt("event_id");
        int entityId = rs.getInt("entity_id");
        return new Event(timestamp, userId, eventType, operation, eventId, entityId);
    }
}
