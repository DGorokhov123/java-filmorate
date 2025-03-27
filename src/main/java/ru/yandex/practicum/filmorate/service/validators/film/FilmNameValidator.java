package ru.yandex.practicum.filmorate.service.validators.film;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;

@Slf4j
public class FilmNameValidator extends FilmAbstractValidator implements FilmValidator {

    @Override
    protected void performValidation(Film film) {
        String name = film.getName();

        if (name == null || name.isBlank()) {
            String reason = "Film name should be specified";
            log.debug("FAILED: {} for {}", reason, film);
            throw new ValidationException(getClass().getSimpleName() + ": " + reason, film);
        }

        log.trace("PASSED");

    }

}
