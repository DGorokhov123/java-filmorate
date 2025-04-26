package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Duration duration;

    private Set<Long> likes = new HashSet<>();
    private Long rating;
    private Set<Long> genres = new HashSet<>();

}
