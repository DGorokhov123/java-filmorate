package ru.yandex.practicum.filmorate.service.validators.user;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;

import java.time.LocalDate;

@Slf4j
public class UserBirthdayValidator extends UserAbstractValidator implements UserValidator {

    @Override
    protected void performValidation(User user) {

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            String reason = "User birthday shouldn't be in future";
            log.debug("FAILED: {} for {}", reason, user);
            throw new ValidationException(getClass().getSimpleName() + ": " + reason, user);
        }

        log.trace("PASSED");

    }

}
