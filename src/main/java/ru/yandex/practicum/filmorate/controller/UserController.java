package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.EventApiDto;
import ru.yandex.practicum.filmorate.model.FilmApiDto;
import ru.yandex.practicum.filmorate.model.UserApiDto;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.Comparator;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FilmService filmService;
    private final EventService eventService;

    // STORAGE OPERATIONS

    @GetMapping
    public Collection<UserApiDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public UserApiDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public UserApiDto deleteUserById(@PathVariable Long id) {
        return userService.deleteUserById(id);
    }

    @PostMapping
    public UserApiDto createUser(@Valid @RequestBody UserApiDto user) {
        return userService.createUser(user);
    }

    @PutMapping
    public UserApiDto updateUser(@Valid @RequestBody UserApiDto user) {
        return userService.updateUser(user);
    }

    // FRIENDS OPERATIONS

    @PutMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.removeFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public Collection<UserApiDto> findFriends(@PathVariable Long userId) {
        return userService.findFriends(userId).stream()
                .sorted(Comparator.comparing(UserApiDto::getId))
                .toList();
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public Collection<UserApiDto> findMutualFriends(@PathVariable Long userId, @PathVariable Long otherId) {
        return userService.findMutualFriends(userId, otherId);
    }

    // RECOMMENDATIONS

    @GetMapping("/{userId}/recommendations")
    public Collection<FilmApiDto> findRecommendations(@PathVariable Long userId) {
        return filmService.findRecommendations(userId);
    }

    // FEED

    @GetMapping("/{userId}/feed")
    public Collection<EventApiDto> getFeed(@PathVariable Long userId) {
        return eventService.getFeed(userId);
    }

}
