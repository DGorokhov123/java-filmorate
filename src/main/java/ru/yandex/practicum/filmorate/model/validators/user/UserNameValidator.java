package ru.yandex.practicum.filmorate.model.validators.user;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;

@Slf4j
public class UserNameValidator extends UserAbstractValidator implements UserValidator {

    @Override
    protected void performValidation(User user) throws ValidationException {

        if (user.getName() == null || user.getName().isBlank()) {
            String reason = "User name is blank. Login will be used instead";
            log.debug("PASSED: {} for {}", reason, user);
            user.setName(user.getLogin());
        } else {
            log.debug("PASSED");
        }

    }

}
