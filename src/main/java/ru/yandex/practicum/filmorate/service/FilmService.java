package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.validators.film.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FilmService {

    private final FilmValidator filmCreateValidator;
    private final FilmValidator filmUpdateValidator;

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final RatingService ratingService;
    private final GenreService genreService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, RatingService ratingService, GenreService genreService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.ratingService = ratingService;
        this.genreService = genreService;

        filmCreateValidator = FilmValidatorBuilder.builder()
                .register(new FilmNullValidator())
                .register(new FilmNameValidator())
                .register(new FilmDescriptionValidator())
                .register(new FilmReleaseDateValidator())
                .register(new FilmDurationValidator())
                .register(new FilmRatingValidator(ratingService.getRatingsMap()))
                .register(new FilmGenresValidator(genreService.getGenresMap()))
                .build();

        filmUpdateValidator = FilmValidatorBuilder.builder()
                .register(new FilmNullValidator())
                .register(new FilmIdValidator())
                .register(new FilmNameValidator())
                .register(new FilmDescriptionValidator())
                .register(new FilmReleaseDateValidator())
                .register(new FilmDurationValidator())
                .register(new FilmRatingValidator(ratingService.getRatingsMap()))
                .register(new FilmGenresValidator(genreService.getGenresMap()))
                .build();

    }


    // STORAGE OPERATIONS


    public Collection<FilmApiDto> getFilms() {
        Collection<Film> films = filmStorage.getFilms();
        Map<Long, Rating> ratings = ratingService.getRatingsMap();
        Map<Long, Genre> genres = genreService.getGenresMap();
        return films.stream().map(f -> FilmMapper.toDto(f, ratings, genres)).toList();
    }

    public FilmApiDto getFilmById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Invalid Film Id");
        Film film = filmStorage.getFilmById(id);
        Map<Long, Rating> ratings = ratingService.getRatingsMap();
        Map<Long, Genre> genres = genreService.getGenresMap();
        return FilmMapper.toDto(film, ratings, genres);
    }

    public FilmApiDto deleteFilmById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Invalid Film Id");
        Film film = filmStorage.deleteFilmById(id);
        log.debug("Deleted film {}", film);
        Map<Long, Rating> ratings = ratingService.getRatingsMap();
        Map<Long, Genre> genres = genreService.getGenresMap();
        return FilmMapper.toDto(film, ratings, genres);
    }

    public FilmApiDto createFilm(FilmApiDto dto) {
        if (dto == null) throw new IllegalArgumentException("Film object shouldn't be null");
        Film film = FilmMapper.toFilm(dto);
        filmCreateValidator.validate(film);
        Film newFilm = filmStorage.createFilm(film);
        log.debug("Created film {}", newFilm);
        Map<Long, Rating> ratings = ratingService.getRatingsMap();
        Map<Long, Genre> genres = genreService.getGenresMap();
        return FilmMapper.toDto(newFilm, ratings, genres);
    }

    public FilmApiDto updateFilm(FilmApiDto dto) {
        if (dto == null) throw new IllegalArgumentException("Film object shouldn't be null");
        if (dto.getId() == null || dto.getId() < 1) throw new IllegalArgumentException("Invalid Film Id");
        Film film = FilmMapper.toFilm(dto);
        filmUpdateValidator.validate(film);
        filmStorage.checkFilmById(film.getId());
        Film newFilm = filmStorage.updateFilm(film);
        log.debug("Updated film {}", newFilm);
        Map<Long, Rating> ratings = ratingService.getRatingsMap();
        Map<Long, Genre> genres = genreService.getGenresMap();
        return FilmMapper.toDto(newFilm, ratings, genres);
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

    public List<FilmApiDto> getPopular(Integer count) {
        if (count == null || count < 0) throw new IllegalArgumentException("count should be a positive integer number");
        List<Film> films = filmStorage.getPopular(count);
        Map<Long, Rating> ratings = ratingService.getRatingsMap();
        Map<Long, Genre> genres = genreService.getGenresMap();
        return films.stream().map(f -> FilmMapper.toDto(f, ratings, genres)).toList();
    }

}
