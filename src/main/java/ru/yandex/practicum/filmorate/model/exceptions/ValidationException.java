package ru.yandex.practicum.filmorate.model.exceptions;

import lombok.Getter;

public class ValidationException extends Exception {

    @Getter
    Object object;

    public ValidationException(String message, Object object) {
        super(message);
        this.object = object;
    }

}
