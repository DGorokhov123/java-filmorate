package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventApiDto;
import ru.yandex.practicum.filmorate.model.EventMapper;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;
import ru.yandex.practicum.filmorate.storage.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventDbStorage eventDbStorage;
    private final UserStorage userStorage;


    public List<EventApiDto> getFeed(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Invalid User id");
        userStorage.checkUserById(id);
        return eventDbStorage.getFeed(id).stream()
                .filter(Objects::nonNull)
                .map(EventMapper::toDto)
                .toList();
    }


    // FRIENDS OPERATIONS


    public void addFriendEvent(Long userId, Long friendId) {
        Event event = makeEvent(userId, friendId, EventType.FRIEND, OperationType.ADD);
        eventDbStorage.createEvent(event);
        log.trace("Added [ADD FRIEND] event to userId = {},  friendId =  {}", userId, friendId);
    }

    public void removeFriendEvent(Long userId, Long friendId) {
        Event event = makeEvent(userId, friendId, EventType.FRIEND, OperationType.REMOVE);
        eventDbStorage.createEvent(event);
        log.trace("Added [REMOVE FRIEND] event to userId = {},  friendId =  {}", userId, friendId);
    }


    // LIKES OPERATIONS


    public void addLikeEvent(Long filmId, Long userId) {
        Event event = makeEvent(userId, filmId, EventType.LIKE, OperationType.ADD);
        eventDbStorage.createEvent(event);
        log.trace("Added [ADD LIKE] event to filmId = {},  userId =  {}", filmId, userId);
    }

    public void removeLikeEvent(Long filmId, Long userId) {
        Event event = makeEvent(userId, filmId, EventType.LIKE, OperationType.REMOVE);
        eventDbStorage.createEvent(event);
        log.trace("Added [REMOVE LIKE] event to filmId = {},  userId =  {}", filmId, userId);
    }


    // REVIEW OPERATIONS


    public void addReviewEvent(Long reviewId, Long userId) {
        Event event = makeEvent(userId, reviewId, EventType.REVIEW, OperationType.ADD);
        eventDbStorage.createEvent(event);
        log.trace("Added [ADD REVIEW] event to reviewId = {},  userId =  {}", reviewId, userId);
    }

    public void removeReviewEvent(Long reviewId, Long userId) {
        Event event = makeEvent(userId, reviewId, EventType.REVIEW, OperationType.REMOVE);
        eventDbStorage.createEvent(event);
        log.trace("Added [REMOVE REVIEW] event to reviewId = {},  userId =  {}", reviewId, userId);
    }

    public void updateReviewEvent(Long reviewId, Long userId) {
        Event event = makeEvent(userId, reviewId, EventType.REVIEW, OperationType.UPDATE);
        eventDbStorage.createEvent(event);
        log.trace("Added [UPDATE REVIEW] event to reviewId = {},  userId =  {}", reviewId, userId);
    }

    private Event makeEvent(Long userId, Long entityId, EventType eventType, OperationType operationType) {
        Event event = new Event();
        event.setUserId(userId);
        event.setEntityId(entityId);
        event.setEventType(eventType);
        event.setOperation(operationType);
        event.setTimestamp(OffsetDateTime.now());
        return event;
    }


}
