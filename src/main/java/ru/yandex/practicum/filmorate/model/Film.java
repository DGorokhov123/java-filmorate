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

    private Long id;

    @NotBlank(message = "@Valid: Film name shouldn't be blank")
    private String name;

    private String description;

    private LocalDate releaseDate;

    @JsonSerialize(using = DurationSerializer.class)
    private Duration duration;

}
