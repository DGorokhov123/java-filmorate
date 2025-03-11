package ru.yandex.practicum.filmorate.model.validators.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;

public interface UserValidator {

    void validate(User user) throws ValidationException;

    void setNext(UserValidator next);

}
