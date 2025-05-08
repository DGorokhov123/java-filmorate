package ru.yandex.practicum.filmorate.model;

public class ReviewMapper {

    public static Review toReview(ReviewApiDto reviewApiDto) {

        return Review.builder()
                .reviewId(reviewApiDto.getReviewId())
                .content(reviewApiDto.getContent())
                .isPositive(reviewApiDto.getIsPositive())
                .userId(reviewApiDto.getUserId())
                .filmId(reviewApiDto.getFilmId())
                .useful(reviewApiDto.getUseful())
                .build();
    }

    public static ReviewApiDto toReviewApiDto(Review review) {

        return ReviewApiDto.builder()
                .reviewId(review.getReviewId())
                .content(review.getContent())
                .isPositive(review.getIsPositive())
                .userId(review.getUserId())
                .filmId(review.getFilmId())
                .useful(review.getUseful())
                .build();
    }
}
