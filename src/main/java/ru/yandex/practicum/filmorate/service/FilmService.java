package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmApiDto;
import ru.yandex.practicum.filmorate.model.FilmMapper;
import ru.yandex.practicum.filmorate.model.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.service.validators.film.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final RatingService ratingService;
    private final GenreService genreService;
    private final EventService eventService;
    private final DirectorService directorService;

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
        addLike(filmId, userId, 6.0);
    }

    public void addLike(Long filmId, Long userId, Double mark) {
        if (filmId == null || filmId < 1) throw new NotFoundException("Invalid Film Id", filmId);
        if (userId == null || userId < 1) throw new NotFoundException("Invalid User Id", userId);
        if (mark == null || mark < 1 || mark > 10) throw new IllegalArgumentException("Invalid Mark value");
        userStorage.checkUserById(userId);
        filmStorage.checkFilmById(filmId);
        filmStorage.addLike(filmId, userId, mark);
        eventService.addLikeEvent(filmId, userId);
        log.debug("Added mark {} to film {} by user {}", mark, filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        if (filmId == null || filmId < 1) throw new NotFoundException("Invalid Film Id", filmId);
        if (userId == null || userId < 1) throw new NotFoundException("Invalid User Id", userId);
        userStorage.checkUserById(userId);
        filmStorage.checkFilmById(filmId);
        filmStorage.removeLike(filmId, userId);
        eventService.removeLikeEvent(filmId, userId);
        log.debug("Removed mark from film {} by user {}", filmId, userId);
    }

    public List<FilmApiDto> getPopular(Integer count, Long genreId, String year) {
        // проверка count
        if (Objects.nonNull(count) && count < 0) throw new IllegalArgumentException("count should be a positive integer number");
        // проверка genreId
        if (Objects.nonNull(genreId) && genreId < 0) throw new IllegalArgumentException("genreId should be a positive integer number");
        // проверка year
        final Year FIRST_FILM_RELEASE_YEAR = Year.of(1985);
        if (Objects.nonNull(year)) {
            try {
                if (Year.parse(year).isBefore(FIRST_FILM_RELEASE_YEAR))
                    throw new IllegalArgumentException("Year should be after or equal 1985");
            } catch (DateTimeParseException ignored) {
                throw new IllegalArgumentException("Year should be a valid info in YYYY format");
            }
        }
        return filmStorage.getPopular(count, genreId, year).stream()
                .filter(Objects::nonNull)
                .map(FilmMapper::toDto)
                .toList();
    }

    // ADD-DIRECTOR FEATURE

    public Collection<FilmApiDto> getDirectorFilm(Integer id, String sortBy) {
        if (id == null || id < 1) throw new NotFoundException("Invalid Director Id", id);
        directorService.findDirectorById(id);
        return filmStorage.getDirectorFilm(id, sortBy).stream()
                .filter(Objects::nonNull)
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

    public Collection<FilmApiDto> getCommonFilms(Long userId, Long friendId) {
        userStorage.checkUserById(userId);
        userStorage.checkUserById(friendId);

        Collection<Long> userFilmsIds = filmStorage.getFilmLikesByUserId(userId);
        Collection<Long> friendFilmsIds = filmStorage.getFilmLikesByUserId(friendId);

        return userFilmsIds.stream()
                .filter(friendFilmsIds::contains)
                .map(filmStorage::getFilmById)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed().thenComparingLong(Film::getId))
                .map(FilmMapper::toDto)
                .toList();
    }

    public Collection<FilmApiDto> searchFilms(String query, String by) {
        if (query == null || query.isBlank()) {
            // Обработка пустого запроса
            return Collections.emptyList();
        }
        Set<Film> films = new HashSet<>();

        if (by == null || by.isBlank()) {
            // По умолчанию ищем только по названию
            films.addAll(filmStorage.findFilmsByTitle(query));
            return films.stream()
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed().thenComparingLong(Film::getId))
                    .map(FilmMapper::toDto)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        Set<String> validParams = new HashSet<>(Arrays.asList("title", "director"));
        Set<String> searchFields = new HashSet<>(Arrays.asList(by.split(",")));

        // Удалить пробелы по краям каждого элемента
        searchFields = searchFields.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        // Проверка: все ли параметры допустимы
        if (!validParams.containsAll(searchFields)) {
            throw new IllegalArgumentException("Параметр 'by' может содержать только 'title' или 'director'");
        }

        if (searchFields.contains("title")) {
            films.addAll(filmStorage.findFilmsByTitle(query));
        }
        if (searchFields.contains("director")) {
            films.addAll(filmStorage.findFilmsByDirector(query));
        }

        return films.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed().thenComparingLong(Film::getId))
                .map(FilmMapper::toDto)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
