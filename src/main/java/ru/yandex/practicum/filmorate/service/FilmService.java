package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.validators.film.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmValidator filmValidator = FilmValidatorBuilder.builder()
            .register(new FilmNameValidator())
            .register(new FilmDescriptionValidator())
            .register(new FilmReleaseDateValidator())
            .register(new FilmDurationValidator())
            .build();

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    // STORAGE OPERATIONS


    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Long id) {
        if (id == null) throw new IllegalArgumentException("Film id shouldn't be null");
        return filmStorage.getFilmById(id);
    }

    public Film deleteFilmById(Long id) {
        if (id == null) throw new IllegalArgumentException("Film id shouldn't be null");
        Film film = filmStorage.deleteFilmById(id);
        log.debug("Deleted film {}", film);
        return film;
    }

    public Film createFilm(Film film) {
        filmValidator.validate(film);
        Film newFilm = filmStorage.createFilm(film);
        log.debug("Created film {}", newFilm);
        return newFilm;
    }

    public Film updateFilm(Film film) {
        filmValidator.validate(film);
        Film newFilm = filmStorage.updateFilm(film);
        log.debug("Updated film {}", newFilm);
        return newFilm;
    }


    // LIKES + POPULAR OPERATIONS


    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        film.getLikes().add(user.getId());
        log.debug("Added like to film {} by user {}", film, user);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        film.getLikes().remove(user.getId());
        log.debug("Removed like from film {} by user {}", film, user);
    }

    public List<Film> getPopular(Integer count) {
        if (count == null || count < 0) throw new IllegalArgumentException("count should be a positive integer number");
        return filmStorage.getFilms().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .toList();
    }

}
