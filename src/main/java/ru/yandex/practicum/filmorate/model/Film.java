package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.yandex.practicum.filmorate.controller.serializers.DurationSerializer;

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

    @JsonSerialize(using = DurationSerializer.class)
    Duration duration;

}
