package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.storage.mappers.EventRowMapper;

import java.sql.PreparedStatement;
import java.util.Collection;

@RequiredArgsConstructor
@Repository
public class EventDbStorage {

    private final JdbcTemplate jdbc;

    public Collection<Event> getFeed(Long id) {
        return jdbc.query(EventRowMapper.GET_FEED_BY_USER_ID_QUERY, new EventRowMapper(), id);
    }

    public Event createEvent(Event event) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbc.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(EventRowMapper.CREATE_EVENT_QUERY, new String[]{"event_id"});
                ps.setLong(1, event.getUserId());
                ps.setLong(2, event.getEntityId());
                ps.setString(3, event.getEventType().toString());
                ps.setString(4, event.getOperation().toString());
                ps.setObject(5, event.getTimestamp());
                return ps;
            }, keyHolder);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Event referential integrity error", event);
        }
        event.setId(keyHolder.getKey().longValue());
        return event;
    }


}
