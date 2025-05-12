package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;

import java.time.OffsetDateTime;

@Data
public class Event {

    private Long id;

    private OffsetDateTime timestamp;

    private Long userId;
    private Long entityId;

    private EventType eventType;
    private OperationType operation;

}
