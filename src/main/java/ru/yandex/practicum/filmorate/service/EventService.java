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
        Event event = new Event();
        event.setUserId(userId);
        event.setEntityId(friendId);
        event.setEventType(EventType.FRIEND);
        event.setOperation(OperationType.ADD);
        event.setTimestamp(OffsetDateTime.now());
        eventDbStorage.createEvent(event);
        log.debug("Added [ADD FRIEND] event to userId = {},  friendId =  {}", userId, friendId);
    }

    public void removeFriendEvent(Long userId, Long friendId) {
        Event event = new Event();
        event.setUserId(userId);
        event.setEntityId(friendId);
        event.setEventType(EventType.FRIEND);
        event.setOperation(OperationType.REMOVE);
        event.setTimestamp(OffsetDateTime.now());
        eventDbStorage.createEvent(event);
        log.debug("Added [REMOVE FRIEND] event to userId = {},  friendId =  {}", userId, friendId);
    }


    // LIKES OPERATIONS


    public void addLikeEvent(Long filmId, Long userId) {
        Event event = new Event();
        event.setUserId(userId);
        event.setEntityId(filmId);
        event.setEventType(EventType.LIKE);
        event.setOperation(OperationType.ADD);
        event.setTimestamp(OffsetDateTime.now());
        eventDbStorage.createEvent(event);
        log.debug("Added [ADD LIKE] event to filmId = {},  userId =  {}", filmId, userId);
    }

    public void removeLikeEvent(Long filmId, Long userId) {
        Event event = new Event();
        event.setUserId(userId);
        event.setEntityId(filmId);
        event.setEventType(EventType.LIKE);
        event.setOperation(OperationType.REMOVE);
        event.setTimestamp(OffsetDateTime.now());
        eventDbStorage.createEvent(event);
        log.debug("Added [REMOVE LIKE] event to filmId = {},  userId =  {}", filmId, userId);
    }


    // REVIEW OPERATIONS


    public void addReviewEvent(Long reviewId, Long userId) {
        Event event = new Event();
        event.setUserId(userId);
        event.setEntityId(reviewId);
        event.setEventType(EventType.REVIEW);
        event.setOperation(OperationType.ADD);
        event.setTimestamp(OffsetDateTime.now());
        eventDbStorage.createEvent(event);
        log.debug("Added [ADD REVIEW] event to reviewId = {},  userId =  {}", reviewId, userId);
    }

    public void removeReviewEvent(Long reviewId, Long userId) {
        Event event = new Event();
        event.setUserId(userId);
        event.setEntityId(reviewId);
        event.setEventType(EventType.REVIEW);
        event.setOperation(OperationType.REMOVE);
        event.setTimestamp(OffsetDateTime.now());
        eventDbStorage.createEvent(event);
        log.debug("Added [REMOVE REVIEW] event to reviewId = {},  userId =  {}", reviewId, userId);
    }

    public void updateReviewEvent(Long reviewId, Long userId) {
        Event event = new Event();
        event.setUserId(userId);
        event.setEntityId(reviewId);
        event.setEventType(EventType.REVIEW);
        event.setOperation(OperationType.UPDATE);
        event.setTimestamp(OffsetDateTime.now());
        eventDbStorage.createEvent(event);
        log.debug("Added [UPDATE REVIEW] event to reviewId = {},  userId =  {}", reviewId, userId);
    }


}
