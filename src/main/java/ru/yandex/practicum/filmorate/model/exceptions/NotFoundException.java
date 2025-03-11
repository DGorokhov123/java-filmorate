package ru.yandex.practicum.filmorate.model.exceptions;

import lombok.Getter;

public class NotFoundException extends Exception {

    @Getter
    Object object;

    public NotFoundException(String message, Object object) {
        super(message);
        this.object = object;
    }

}
