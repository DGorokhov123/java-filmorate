package ru.yandex.practicum.filmorate.service.validators.film;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;

@Slf4j
public class FilmIdValidator extends FilmAbstractValidator {

    @Override
    protected void performValidation(Film film) {

        if (film.getId() == null) {
            String reason = "Film id shouldn't be null";
            log.debug("FAILED: {}", reason);
            throw new ValidationException(getClass().getSimpleName() + ": " + reason, film);
        }

        log.trace("PASSED");

    }

}
