package ru.yandex.practicum.filmorate.model;

public class RatingMapper {

    public static RatingApiDto toDto(Rating rating) {
        RatingApiDto dto = new RatingApiDto();
        dto.setId(rating.getId());
        dto.setName(rating.getName());
        return dto;
    }

    public static Rating toRating(RatingApiDto dto) {
        Rating rating = new Rating();
        rating.setId(dto.getId());
        rating.setName(dto.getName());
        return rating;
    }

}
