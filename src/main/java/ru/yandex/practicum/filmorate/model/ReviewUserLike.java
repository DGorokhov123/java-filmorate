package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class ReviewUserLike {
    private Long reviewId;
    private Long userId;
    private int reaction;
}
