package ru.yandex.practicum.filmorate.model.validators.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;

public abstract class FilmAbstractValidator implements FilmValidator {

    private FilmValidator next;

    @Override
    public void setNext(FilmValidator next) {
        this.next = next;
    }

    @Override
    public void validate(Film film) throws ValidationException {
        performValidation(film);
        if (next != null) next.validate(film);
    }

    protected abstract void performValidation(Film film) throws ValidationException;

}
