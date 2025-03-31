package ru.yandex.practicum.filmorate.service.validators.film;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;

@Slf4j
public class FilmNullValidator extends FilmAbstractValidator {

    @Override
    protected void performValidation(Film film) {

        if (film == null) {
            String reason = "Film shouldn't be null";
            log.debug("FAILED: {}", reason);
            throw new ValidationException(getClass().getSimpleName() + ": " + reason, null);
        }

        log.trace("PASSED");

    }

}
