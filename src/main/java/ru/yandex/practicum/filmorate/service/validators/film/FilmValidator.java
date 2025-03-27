package ru.yandex.practicum.filmorate.service.validators.film;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmValidator {

    void validate(Film film);

    void setNext(FilmValidator next);

}
