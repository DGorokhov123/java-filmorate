package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.yandex.practicum.filmorate.controller.serializers.DurationDeserializer;
import ru.yandex.practicum.filmorate.controller.serializers.DurationSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class FilmApiDto {

    private Long id;

    @NotBlank(message = "@Valid: Film name shouldn't be blank")
    private String name;

    private String description;
    private LocalDate releaseDate;

    @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration duration;

    private Set<Long> likes = new HashSet<>();

    private Rating mpa;

    private Set<Genre> genres = new HashSet<>();

}
