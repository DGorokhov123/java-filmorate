package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    Instant timestamp;

    HttpStatus status;

    String error;

    String message;

    String path;

}
