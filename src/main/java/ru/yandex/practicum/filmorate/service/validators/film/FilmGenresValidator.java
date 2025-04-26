package ru.yandex.practicum.filmorate.service.validators.film;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.exceptions.NotFoundException;

import java.util.Map;

@Slf4j
public class FilmGenresValidator extends FilmAbstractValidator implements FilmValidator {

    private final Map<Long, Genre> genres;

    public FilmGenresValidator(Map<Long, Genre> genres) {
        this.genres = genres;
    }

    @Override
    protected void performValidation(Film film) {

        for (Long genreId : film.getGenres()) {
            if (genreId != null && genres.get(genreId) == null) {
                String reason = "Film genre " + genreId + " not found";
                log.debug("FAILED: {} for {}", reason, film);
                throw new NotFoundException(getClass().getSimpleName() + ": " + reason, film);
            }
        }


        log.trace("PASSED");

    }

}
