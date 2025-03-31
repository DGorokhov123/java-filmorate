package ru.yandex.practicum.filmorate.service.validators.film;

public class FilmValidatorBuilder {

    private FilmValidator head;
    private FilmValidator tail;

    public static FilmValidatorBuilder builder() {
        return new FilmValidatorBuilder();
    }

    public FilmValidatorBuilder register(FilmValidator validator) {
        if (tail == null) {
            head = validator;
        } else {
            tail.setNext(validator);
        }
        tail = validator;
        return this;
    }

    public FilmValidator build() {
        if (head == null) throw new IllegalStateException("At least one validator must be added");
        return head;
    }

}
