package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Collection<Film> getFilms();

    Film getFilmById(Long id);

    void checkFilmById(Long id);

    Film deleteFilmById(Long id);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    List<Film> getPopular(Integer count);

    //add-director feature
    Collection<Film> getDirectorFilm(Integer id, String sortBy);

    List<Film> getRecommendations(Long userId);

    Collection<Long> getFilmLikesByUserId(Long userId);
    Collection<Film> findFilmsByDirector(String query);

    Collection<Film> findFilmsByTitle(String query);

}
