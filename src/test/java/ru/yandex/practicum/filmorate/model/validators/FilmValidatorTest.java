package ru.yandex.practicum.filmorate.model.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.validators.film.*;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmValidatorTest {

    private Film film;

    private final FilmValidator filmValidator = FilmValidatorBuilder.builder()
            .register(new FilmNameValidator())
            .register(new FilmDescriptionValidator())
            .register(new FilmReleaseDateValidator())
            .register(new FilmDurationValidator())
            .build();

    @BeforeEach
    void init() {
        film = new Film();
        film.setId(3L);
        film.setName("Inception");
        film.setDescription("A thief enters dreams to steal secrets, facing blurred lines between reality and illusion.");
        film.setReleaseDate(LocalDate.of(2010, 7, 8));
        film.setDuration(Duration.parse("PT2H28M"));
    }

    @Test
    void passesAllValidators() {
        assertDoesNotThrow(() -> {
            filmValidator.validate(film);
        });
    }

    @Test
    void nameValidator() {
        film.setName("");
        assertThrows(ValidationException.class, () -> {
            filmValidator.validate(film);
        });

        film.setName(null);
        assertThrows(ValidationException.class, () -> {
            filmValidator.validate(film);
        });
    }

    @Test
    void descriptionValidator() {
        film.setDescription("A skilled thief enters dreams to steal some secrets. " +
                "Hired for an impossible job, he dives into layers of reality and illusion, battling to discern " +
                "truth as his mind bends under the weight of deception.");
        assertThrows(ValidationException.class, () -> {
            filmValidator.validate(film);
        });

        film.setDescription("");
        assertDoesNotThrow(() -> {
            filmValidator.validate(film);
        });

        film.setDescription(null);
        assertDoesNotThrow(() -> {
            filmValidator.validate(film);
        });
    }

    @Test
    void releaseDateValidator() {
        film.setReleaseDate(LocalDate.of(1890, 7, 8));
        assertThrows(ValidationException.class, () -> {
            filmValidator.validate(film);
        });

        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertDoesNotThrow(() -> {
            filmValidator.validate(film);
        });

        film.setReleaseDate(null);
        assertDoesNotThrow(() -> {
            filmValidator.validate(film);
        });
    }

    @Test
    void durationValidator() {
        film.setDuration(Duration.ZERO);
        assertThrows(ValidationException.class, () -> {
            filmValidator.validate(film);
        });

        film.setDuration(Duration.ofHours(-1L));
        assertThrows(ValidationException.class, () -> {
            filmValidator.validate(film);
        });

        film.setDuration(null);
        assertDoesNotThrow(() -> {
            filmValidator.validate(film);
        });
    }

}