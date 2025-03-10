package ru.yandex.practicum.filmorate.model.validators.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;

public interface FilmValidator {

    void validate(Film film) throws ValidationException;

    void setNext(FilmValidator next);

}
