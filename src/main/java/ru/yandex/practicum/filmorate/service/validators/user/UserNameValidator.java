package ru.yandex.practicum.filmorate.service.validators.user;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
public class UserNameValidator extends UserAbstractValidator implements UserValidator {

    @Override
    protected void performValidation(User user) {

        if (user.getName() == null || user.getName().isBlank()) {
            String reason = "User name is blank. Login will be used instead";
            log.debug("MODIFIED: {} for {}", reason, user);
            user.setName(user.getLogin());
        } else {
            log.trace("PASSED");
        }

    }

}
