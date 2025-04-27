package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.UserApiDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


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
        return userService.findFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public Collection<UserApiDto> findMutualFriends(@PathVariable Long userId, @PathVariable Long otherId) {
        return userService.findMutualFriends(userId, otherId);
    }

}
