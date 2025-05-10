package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.ReviewApiDto;
import ru.yandex.practicum.filmorate.model.ReviewMapper;
import ru.yandex.practicum.filmorate.model.ReviewUserLike;
import ru.yandex.practicum.filmorate.storage.ReviewsDBStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private static final int USER_LIKE = 10;
    private static final int USER_DISLIKE = 1;
    private static final int USER_LIKE_START = 6;

    private final ReviewsDBStorage reviewsStorage;

    public ReviewApiDto createReview(@Valid ReviewApiDto reviewDTO) {
        return ReviewMapper.toReviewApiDto(reviewsStorage.createReview(
                        ReviewMapper.toReview(reviewDTO)),
                0); // при создании нового отзыва его полезность всегда = 0
    }

    public ReviewApiDto getReviewById(Long id) {
        return ReviewMapper.toReviewApiDto(reviewsStorage.getReviewById(id), getReviewUseful(id));
    }


    public ReviewApiDto updateReview(@Valid ReviewApiDto reviewApiDto) {
        return ReviewMapper.toReviewApiDto(reviewsStorage.updateReview(
                ReviewMapper.toReview(reviewApiDto)), getReviewUseful(reviewApiDto.getReviewId()));
    }

    public Collection<ReviewApiDto> getReviewByFilmd(Long filmId, Integer count) {
        return reviewsStorage.getReviewsByFilmId(filmId, count).stream()
                .map(review -> ReviewMapper.toReviewApiDto(review, getReviewUseful(review.getReviewId())))
                .collect(Collectors.toSet());
    }

    public ReviewApiDto deleteReviewById(Long id) {

        int useful = getReviewUseful(id);

        reviewsStorage.deleteReviewLikes(id); // перед удалением обзора удалить его лайки

        return ReviewMapper.toReviewApiDto(reviewsStorage.deleteReviewById(id), useful);
    }

    public ReviewApiDto addLike(Long reviewId, Long userId) {

        int reaction = getUserReaction(reviewId, userId);

        // если лайк уже есть, то это ошибка
        if (reaction == 1) {
            throw new ValidationException("User id=" + userId + " already has like on review id=" + reviewId);
        }

        // если есть дизлайк, то его нужно удалить
        if (reaction == -1) {
            reviewsStorage.deleteUserReaction(reviewId, userId);
        }

        reviewsStorage.addUserReaction(reviewId, userId, USER_LIKE);


        return ReviewMapper.toReviewApiDto(
                reviewsStorage.getReviewById(reviewId), getReviewUseful(reviewId));
    }


    public ReviewApiDto addDislike(Long reviewId, Long userId) {

        int reaction = getUserReaction(reviewId, userId);

        // если дизлайк уже есть, то это ошибка
        if (reaction == -1) {
            throw new ValidationException("User id=" + userId + " already has dislike on review id=" + reviewId);
        }

        // если есть лайк, то его нужно удалить
        if (reaction == 1) {
            reviewsStorage.deleteUserReaction(reviewId, userId);
        }

        reviewsStorage.addUserReaction(reviewId, userId, USER_DISLIKE);


        return ReviewMapper.toReviewApiDto(
                reviewsStorage.getReviewById(reviewId), getReviewUseful(reviewId));
    }

    public ReviewApiDto removeLike(Long reviewId, Long userId) {
        int reaction = getUserReaction(reviewId, userId);

        // если лайка нет, то это ошибка
        if (reaction == 0) {
            throw new ValidationException("User id=" + userId + " didn't has like on review id=" + reviewId);
        }

        // если есть лайк, то его нужно удалить, если есть дизлайк, ничего не делаем
        if (reaction == 1) {
            reviewsStorage.deleteUserReaction(reviewId, userId);
        }

        return ReviewMapper.toReviewApiDto(
                reviewsStorage.getReviewById(reviewId), getReviewUseful(reviewId));
    }

    public ReviewApiDto removeDislike(Long reviewId, Long userId) {
        int reaction = getUserReaction(reviewId, userId);

        // если лайка нет, то это ошибка
        if (reaction == 0) {
            throw new ValidationException("User id=" + userId + " didn't has dislike on review id=" + reviewId);
        }

        // если есть дизлайк, то его нужно удалить, если есть лайк, ничего не делаем
        if (reaction == -1) {
            reviewsStorage.deleteUserReaction(reviewId, userId);
        }

        return ReviewMapper.toReviewApiDto(
                reviewsStorage.getReviewById(reviewId), getReviewUseful(reviewId));
    }

    private int getReviewUseful(Long id) {
        Collection<ReviewUserLike> reviewLikes = reviewsStorage.getReviewReactions(id);

        int useful = 0;

        for (ReviewUserLike like : reviewLikes) {
            if (like.getReaction() >= USER_LIKE_START) { // заложено под развитие
                useful++;
            } else {
                useful--;
            }
        }
        return useful;
    }

    private int getUserReaction(Long id, Long userId) {


        Optional<ReviewUserLike> userReaction = reviewsStorage.getUserReaction(id, userId);
        if (userReaction.isPresent()) {
            if (userReaction.get().getReaction() >= USER_LIKE_START) { // заложено под развитие
                return 1;
            } else return -1;
        }

        return 0;
    }


}
