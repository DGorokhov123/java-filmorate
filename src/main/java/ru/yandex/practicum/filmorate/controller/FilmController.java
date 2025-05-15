package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.FilmApiDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    // STORAGE OPERATIONS

    @GetMapping
    public Collection<FilmApiDto> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public FilmApiDto getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @DeleteMapping("/{id}")
    public FilmApiDto deleteFilmById(@PathVariable Long id) {
        return filmService.deleteFilmById(id);
    }

    @PostMapping
    public FilmApiDto createFilm(@Valid @RequestBody FilmApiDto film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public FilmApiDto updateFilm(@Valid @RequestBody FilmApiDto film) {
        return filmService.updateFilm(film);
    }

    // LIKES + POPULAR OPERATIONS

    @PutMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.removeLike(filmId, userId);
    }

    @PutMapping("/{filmId}/like/{userId}/{mark}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addMark(@PathVariable Long filmId, @PathVariable Long userId, @PathVariable Integer mark) {
        filmService.addLike(filmId, userId, mark);
    }

    // ADD-MOST-POPULARS
    // GET /films/popular?count={limit}&genreId={genreId}&year={year}
    @GetMapping("/popular")
    public Collection<FilmApiDto> getPopular(@RequestParam(defaultValue = "10") Integer count,
                                             @RequestParam(required = false) Long genreId,
                                             @RequestParam(required = false) String year) {
        return filmService.getPopular(count, genreId, year);
    }

    // ADD-DIRECTOR FEATURE
    // GET /films/director/{directorId}?sortBy=[year,likes]
    @GetMapping("/director/{directorId}")
    public Collection<FilmApiDto> getDirectorFilm(@PathVariable("directorId") Integer id,
                                                  @RequestParam(value = "sortBy",
                                                          defaultValue = "year", required = false) String sortBy) {
        return filmService.getDirectorFilm(id, sortBy);
    }

    // SEARCH

    @GetMapping("/search")
    public Collection<FilmApiDto> searchFilms(
            @RequestParam String query,
            @RequestParam String by) {
        return filmService.searchFilms(query, by);
    }

    @GetMapping("/common")
    public Collection<FilmApiDto> getCommonFilms(@RequestParam() Long userId,
                                                 @RequestParam Long friendId) {
        return filmService.getCommonFilms(userId, friendId);

    }

}
