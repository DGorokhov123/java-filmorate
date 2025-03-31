package ru.yandex.practicum.filmorate.service.validators.user;

import ru.yandex.practicum.filmorate.model.User;

public interface UserValidator {

    void validate(User user);

    void setNext(UserValidator next);

}
