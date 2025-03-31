package ru.yandex.practicum.filmorate.service.validators.user;

import ru.yandex.practicum.filmorate.model.User;

public abstract class UserAbstractValidator implements UserValidator {

    private UserValidator next;

    @Override
    public void setNext(UserValidator next) {
        this.next = next;
    }

    @Override
    public void validate(User user) {
        performValidation(user);
        if (next != null) next.validate(user);
    }

    protected abstract void performValidation(User user);

}
