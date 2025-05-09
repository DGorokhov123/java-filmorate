package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;

@Data
public class EventApiDto {

    private Long eventId;

    private Long timestamp;

    private Long userId;
    private Long entityId;

    private EventType eventType;
    private OperationType operation;


}
