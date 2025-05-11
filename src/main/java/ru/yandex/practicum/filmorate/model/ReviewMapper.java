package ru.yandex.practicum.filmorate.model;

public class ReviewMapper {

    public static Review toReview(ReviewApiDto reviewApiDto) {

        return Review.builder()
                .reviewId(reviewApiDto.getReviewId())
                .content(reviewApiDto.getContent())
                .isPositive(reviewApiDto.getIsPositive())
                .userId(reviewApiDto.getUserId())
                .filmId(reviewApiDto.getFilmId())
                .build();
    }

    public static ReviewApiDto toReviewApiDto(Review review, Integer useFul) {

        return ReviewApiDto.builder()
                .reviewId(review.getReviewId())
                .content(review.getContent())
                .isPositive(review.getIsPositive())
                .userId(review.getUserId())
                .filmId(review.getFilmId())
                .useful(useFul)
                .build();
    }
}
