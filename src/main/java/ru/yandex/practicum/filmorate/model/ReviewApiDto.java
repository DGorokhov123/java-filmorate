package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NonNull;

@Data
public class ReviewApiDto {

    private Long reviewId;

    @NonNull
    private String content;

    @NonNull
    private Boolean isPositive;

    @Positive
    @NonNull
    private Long userId;

    @Positive
    @NonNull
    private Long filmId;

    int useful;

}
