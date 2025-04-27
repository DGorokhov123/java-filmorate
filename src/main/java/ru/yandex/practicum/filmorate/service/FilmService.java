package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.validators.film.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final RatingService ratingService;
    private final GenreService genreService;

    private final FilmValidator filmCreateValidator = FilmValidatorBuilder.builder()
            .register(new FilmNullValidator())
            .register(new FilmNameValidator())
            .register(new FilmDescriptionValidator())
            .register(new FilmReleaseDateValidator())
            .register(new FilmDurationValidator())
            .build();

    private final FilmValidator filmUpdateValidator = FilmValidatorBuilder.builder()
            .register(new FilmNullValidator())
            .register(new FilmIdValidator())
            .register(new FilmNameValidator())
            .register(new FilmDescriptionValidator())
            .register(new FilmReleaseDateValidator())
            .register(new FilmDurationValidator())
            .build();


    // STORAGE OPERATIONS


    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Invalid Film Id");
        return filmStorage.getFilmById(id);
    }

    public Film deleteFilmById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Invalid Film Id");
        Film film = filmStorage.deleteFilmById(id);
        log.debug("Deleted film {}", film);
        return film;
    }

    public Film createFilm(Film film) {
        if (film == null) throw new IllegalArgumentException("Film object shouldn't be null");
        filmCreateValidator.validate(film);
        ratingService.checkFilmRating(film);
        genreService.checkFilmGenres(film);
        Film newFilm = filmStorage.createFilm(film);
        log.debug("Created film {}", newFilm);
        return newFilm;
    }

    public Film updateFilm(Film film) {
        if (film == null) throw new IllegalArgumentException("Film object shouldn't be null");
        if (film.getId() == null || film.getId() < 1) throw new IllegalArgumentException("Invalid Film Id");
        filmUpdateValidator.validate(film);
        ratingService.checkFilmRating(film);
        genreService.checkFilmGenres(film);
        filmStorage.checkFilmById(film.getId());
        Film newFilm = filmStorage.updateFilm(film);
        log.debug("Updated film {}", newFilm);
        return newFilm;
    }


    // LIKES + POPULAR OPERATIONS


    public void addLike(Long filmId, Long userId) {
        if (filmId == null || filmId < 1) throw new IllegalArgumentException("Invalid Film Id");
        if (userId == null || userId < 1) throw new IllegalArgumentException("Invalid User Id");
        userStorage.checkUserById(userId);
        filmStorage.checkFilmById(filmId);
        filmStorage.addLike(filmId, userId);
        log.debug("Added like to film {} by user {}", filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        if (filmId == null || filmId < 1) throw new IllegalArgumentException("Invalid Film Id");
        if (userId == null || userId < 1) throw new IllegalArgumentException("Invalid User Id");
        userStorage.checkUserById(userId);
        filmStorage.checkFilmById(filmId);
        filmStorage.removeLike(filmId, userId);
        log.debug("Removed like from film {} by user {}", filmId, userId);
    }

    public List<Film> getPopular(Integer count) {
        if (count == null || count < 0) throw new IllegalArgumentException("count should be a positive integer number");
        return filmStorage.getPopular(count);
    }

}
