package ru.yandex.practicum.filmorate.model.validators.user;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;

@Slf4j
public class UserLoginValidator extends UserAbstractValidator implements UserValidator {

    @Override
    protected void performValidation(User user) throws ValidationException {
        String login = user.getLogin();

        if (login == null || login.isBlank()) {
            String reason = "User login shouldn't be empty";
            log.debug("FAILED: {} for {}", reason, user);
            throw new ValidationException(getClass().getSimpleName() + ": " + reason, user);
        }

        if (login.indexOf(' ') >= 0 || login.indexOf('\t') >= 0) {
            String reason = "User login shouldn't contain spaces or tabs";
            log.debug("FAILED: {} for {}", reason, user);
            throw new ValidationException(getClass().getSimpleName() + ": " + reason, user);
        }

        log.debug("PASSED");

    }

}
