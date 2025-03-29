package ru.yandex.practicum.filmorate.service.validators.user;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;

@Slf4j
public class UserNullValidator extends UserAbstractValidator {

    @Override
    protected void performValidation(User user) {

        if (user == null) {
            String reason = "User shouldn't be null";
            log.debug("FAILED: {}", reason);
            throw new ValidationException(getClass().getSimpleName() + ": " + reason, null);
        }

        log.trace("PASSED");

    }

}
