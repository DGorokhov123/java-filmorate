package ru.yandex.practicum.filmorate.service.validators.film;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;

import java.time.Duration;

@Slf4j
public class FilmDurationValidator extends FilmAbstractValidator implements FilmValidator {

    @Override
    protected void performValidation(Film film) {

        Duration duration = film.getDuration();

        if (duration != null && !duration.isPositive()) {
            String reason = "Duration should be positive";
            log.debug("FAILED: {} for {}", reason, film);
            throw new ValidationException(getClass().getSimpleName() + ": " + reason, film);
        }

        log.trace("PASSED");

    }

}
