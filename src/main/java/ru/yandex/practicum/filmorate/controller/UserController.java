package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.validators.user.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    UserValidator userValidator = UserValidatorBuilder.builder()
            .register(new UserEmailValidator())
            .register(new UserLoginValidator())
            .register(new UserNameValidator())
            .register(new UserBirthdayValidator())
            .build();

    Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) throws NotFoundException {
        User user = users.get(id);
        if (user == null) throw new NotFoundException("User not found", id);
        return user;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) throws ValidationException {
        userValidator.validate(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Added user {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws ValidationException, NotFoundException {
        userValidator.validate(user);
        User oldUser = users.get(user.getId());
        if (oldUser == null) throw new NotFoundException("User not found", user);
        oldUser.setEmail(user.getEmail());
        oldUser.setLogin(user.getLogin());
        oldUser.setName(user.getName());
        oldUser.setBirthday(user.getBirthday());
        log.info("Updated user {}", oldUser);
        return oldUser;
    }

    private Long getNextId() {
        return users.keySet().stream().mapToLong(id -> id).max().orElse(0) + 1;
    }

}
