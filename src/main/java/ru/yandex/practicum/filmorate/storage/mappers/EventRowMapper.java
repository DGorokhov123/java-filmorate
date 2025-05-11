package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class EventRowMapper implements RowMapper<Event> {

    public static String GET_FEED_BY_USER_ID_QUERY = """
            WITH target AS (
                SELECT CAST(? AS BIGINT) AS target_id
            )
            SELECT e.event_id, e.user_id, e.entity_id, e.event_type, e.operation, e.created_at
            FROM events AS e
            LEFT JOIN friends AS f ON f.friend_id = e.user_id
            WHERE f.user_id = (SELECT target_id FROM target)
            OR e.user_id = (SELECT target_id FROM target)
            ORDER BY e.event_id ASC;
            """;

    public static String CREATE_EVENT_QUERY = """
            INSERT INTO events (user_id, entity_id, event_type, operation, created_at)
            VALUES (?, ?, ?, ?, ?);
            """;


    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        Event event = new Event();
        event.setId(rs.getLong("event_id"));
        event.setUserId(rs.getLong("user_id"));
        event.setEntityId(rs.getLong("entity_id"));
        event.setEventType(EventType.valueOf(rs.getString("event_type")));
        event.setOperation(OperationType.valueOf(rs.getString("operation")));
        event.setTimestamp(rs.getObject("created_at", OffsetDateTime.class));
        return event;
    }

}
