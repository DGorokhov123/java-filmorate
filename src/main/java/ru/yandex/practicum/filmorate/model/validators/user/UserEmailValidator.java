package ru.yandex.practicum.filmorate.model.validators.user;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;

import java.util.regex.Pattern;

@Slf4j
public class UserEmailValidator extends UserAbstractValidator implements UserValidator {

    private final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Override
    protected void performValidation(User user) throws ValidationException {

        if ( user.getEmail() == null || !EMAIL_PATTERN.matcher(user.getEmail()).matches() ) {
            String reason = "User email doesn't match email mask";
            log.debug("FAILED: {} for {}", reason, user);
            throw new ValidationException(getClass().getSimpleName() + ": " + reason, user);
        }

        log.debug("PASSED");

    }

}
