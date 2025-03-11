package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.validators.film.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmValidator filmValidator = FilmValidatorBuilder.builder()
            .register(new FilmNameValidator())
            .register(new FilmDescriptionValidator())
            .register(new FilmReleaseDateValidator())
            .register(new FilmDurationValidator())
            .build();

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) throws NotFoundException {
        Film film = films.get(id);
        if (film == null) throw new NotFoundException("Film not found", id);
        return film;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) throws ValidationException {
        filmValidator.validate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Added film {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws NotFoundException, ValidationException {
        filmValidator.validate(film);
        Film oldFilm = films.get(film.getId());
        if (oldFilm == null) throw new NotFoundException("Film not found", film);
        oldFilm.setName(film.getName());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setReleaseDate(film.getReleaseDate());
        oldFilm.setDuration(film.getDuration());
        log.info("Updated film {}", oldFilm);
        return oldFilm;
    }

    private Long getNextId() {
        return films.keySet().stream().mapToLong(id -> id).max().orElse(0) + 1;
    }

}
