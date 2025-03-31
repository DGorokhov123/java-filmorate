package ru.yandex.practicum.filmorate.service.validators.film;

import ru.yandex.practicum.filmorate.model.Film;

public abstract class FilmAbstractValidator implements FilmValidator {

    private FilmValidator next;

    @Override
    public void setNext(FilmValidator next) {
        this.next = next;
    }

    @Override
    public void validate(Film film) {
        performValidation(film);
        if (next != null) next.validate(film);
    }

    protected abstract void performValidation(Film film);

}
