package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmApiDto;
import ru.yandex.practicum.filmorate.model.FilmMapper;
import ru.yandex.practicum.filmorate.service.validators.film.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

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


    public Collection<FilmApiDto> getFilms() {
        return filmStorage.getFilms().stream()
                .filter(Objects::nonNull)
                .map(FilmMapper::toDto)
                .toList();
    }

    public FilmApiDto getFilmById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Invalid Film Id");
        Film film = filmStorage.getFilmById(id);
        FilmApiDto dto = FilmMapper.toDto(film);
        return dto;
    }

    public FilmApiDto deleteFilmById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Invalid Film Id");
        Film film = filmStorage.deleteFilmById(id);
        log.debug("Deleted film {}", film);
        return FilmMapper.toDto(film);
    }

    public FilmApiDto createFilm(FilmApiDto dto) {
        if (dto == null) throw new IllegalArgumentException("Film object shouldn't be null");
        Film film = FilmMapper.toFilm(dto);
        filmCreateValidator.validate(film);
        ratingService.checkFilmRating(film);
        genreService.checkFilmGenres(film);
        Film newFilm = filmStorage.createFilm(film);
        log.debug("Created film {}", newFilm);
        return FilmMapper.toDto(newFilm);
    }

    public FilmApiDto updateFilm(FilmApiDto dto) {
        if (dto == null) throw new IllegalArgumentException("Film object shouldn't be null");
        if (dto.getId() == null || dto.getId() < 1) throw new IllegalArgumentException("Invalid Film Id");
        Film film = FilmMapper.toFilm(dto);
        filmUpdateValidator.validate(film);
        ratingService.checkFilmRating(film);
        genreService.checkFilmGenres(film);
        filmStorage.checkFilmById(film.getId());
        Film newFilm = filmStorage.updateFilm(film);
        log.debug("Updated film {}", newFilm);
        return FilmMapper.toDto(newFilm);
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
        return filmStorage.getPopular(count).stream()
                .filter(Objects::nonNull)
                .map(FilmMapper::toDto)
                .toList();
    }

    // ADD-DIRECTOR FEATURE

    public Collection<FilmApiDto> getDirectorFilm(Integer id, String sortBy) {
        return filmStorage.getDirectorFilm(id, sortBy).stream()
                .map(FilmMapper::toDto)
                .toList();
    }




    // RECOMMENDATIONS


    public Collection<FilmApiDto> findRecommendations(Long userId) {
        if (userId == null || userId < 1) throw new IllegalArgumentException("Invalid User Id");
        userStorage.checkUserById(userId);
        List<Film> films = filmStorage.getRecommendations(userId);
        return films.stream()
                .filter(Objects::nonNull)
                .map(FilmMapper::toDto)
                .toList();
    }

}
