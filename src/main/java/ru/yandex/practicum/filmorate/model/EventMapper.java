package ru.yandex.practicum.filmorate.model;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class EventMapper {

    public static EventApiDto toDto(Event event) {
        EventApiDto dto = new EventApiDto();

        dto.setEventId(event.getId());
        dto.setUserId(event.getUserId());
        dto.setEntityId(event.getEntityId());
        dto.setEventType(event.getEventType());
        dto.setOperation(event.getOperation());

        OffsetDateTime time = event.getTimestamp();
        if (time != null) {
            dto.setTimestamp(time.toInstant().toEpochMilli());
        }

        return dto;
    }

    public static Event toEvent(EventApiDto dto) {
        Event event = new Event();

        event.setId(dto.getEventId());
        event.setUserId(dto.getUserId());
        event.setEntityId(dto.getEntityId());
        event.setEventType(dto.getEventType());
        event.setOperation(dto.getOperation());

        Long time = dto.getTimestamp();
        if (time != null) {
            event.setTimestamp(OffsetDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneOffset.UTC));
        }

        return event;
    }


}
