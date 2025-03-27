package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> getFilms();

    Film getFilmById(Long id);

    Film deleteFilmById(Long id);

    Film createFilm(Film film);

    Film updateFilm(Film film);

}
