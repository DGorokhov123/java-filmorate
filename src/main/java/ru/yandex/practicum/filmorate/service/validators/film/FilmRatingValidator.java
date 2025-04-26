package ru.yandex.practicum.filmorate.service.validators.film;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.exceptions.NotFoundException;

import java.util.Map;

@Slf4j
public class FilmRatingValidator extends FilmAbstractValidator implements FilmValidator {

    private final Map<Long, Rating> ratings;

    public FilmRatingValidator(Map<Long, Rating> ratings) {
        this.ratings = ratings;
    }

    @Override
    protected void performValidation(Film film) {
        Long ratingId = film.getRating();

        if (ratingId != null && ratings.get(ratingId) == null) {
            String reason = "Film rating " + ratingId + " not found";
            log.debug("FAILED: {} for {}", reason, film);
            throw new NotFoundException(getClass().getSimpleName() + ": " + reason, film);
        }

        log.trace("PASSED");

    }

}
