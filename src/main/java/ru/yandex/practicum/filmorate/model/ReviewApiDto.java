package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class ReviewApiDto {

    private Long reviewId;

    @NonNull
    private String content;

    @NonNull
    private Boolean isPositive;

    @NonNull
    private Long userId;

    @NonNull
    private Long filmId;

    int useful;

}
