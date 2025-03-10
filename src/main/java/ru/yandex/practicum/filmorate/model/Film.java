package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {

    long id;

    @NotBlank(message = "@Valid: Film name shouldn't be blank")
    String name;

    String description;

    LocalDate releaseDate;

    Duration duration;

}
