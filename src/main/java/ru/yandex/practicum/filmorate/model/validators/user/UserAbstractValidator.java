package ru.yandex.practicum.filmorate.model.validators.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;

public abstract class UserAbstractValidator implements UserValidator {

    private UserValidator next;

    @Override
    public void setNext(UserValidator next) {
        this.next = next;
    }

    @Override
    public void validate(User user) throws ValidationException {
        performValidation(user);
        if (next != null) next.validate(user);
    }

    protected abstract void performValidation(User user) throws ValidationException;

}
