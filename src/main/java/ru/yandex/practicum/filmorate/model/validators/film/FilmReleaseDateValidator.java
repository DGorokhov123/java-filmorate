package ru.yandex.practicum.filmorate.model.validators.film;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;

import java.time.LocalDate;

@Slf4j
public class FilmReleaseDateValidator extends FilmAbstractValidator implements FilmValidator {

    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    protected void performValidation(Film film) throws ValidationException {

        LocalDate date = film.getReleaseDate();

        if (date != null && date.isBefore(MIN_RELEASE_DATE)) {
            String reason = "Film release date shouldn't be earlier than " + MIN_RELEASE_DATE;
            log.debug("FAILED: {} for {}", reason, film);
            throw new ValidationException(getClass().getSimpleName() + ": " + reason, film);
        }

        log.debug("PASSED");

    }

}
