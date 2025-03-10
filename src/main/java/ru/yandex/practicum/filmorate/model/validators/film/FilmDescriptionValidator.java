package ru.yandex.practicum.filmorate.model.validators.film;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;

@Slf4j
public class FilmDescriptionValidator extends FilmAbstractValidator implements FilmValidator {

    public static final int MAX_DESCRIPTION_LENGTH = 200;

    @Override
    protected void performValidation(Film film) throws ValidationException {

        String description = film.getDescription();

        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
            String reason = "Film description length should be less than " + MAX_DESCRIPTION_LENGTH + " symbols";
            log.debug("FAILED: {} for {}", reason, film);
            throw new ValidationException(getClass().getSimpleName() + ": " + reason, film);
        }

        log.debug("PASSED");

    }

}
