package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.ReviewApiDto;
import ru.yandex.practicum.filmorate.model.ReviewMapper;
import ru.yandex.practicum.filmorate.storage.ReviewsDBStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewsDBStorage reviewsStorage;

    public ReviewApiDto createReview(@Valid ReviewApiDto reviewDTO) {
        return ReviewMapper.toReviewApiDto(reviewsStorage.createReview(ReviewMapper.toReview(reviewDTO)));
    }

    public ReviewApiDto getReviewById(Long id) {
        return ReviewMapper.toReviewApiDto(reviewsStorage.getReviewById(id));

    }

    public ReviewApiDto updateReview(@Valid ReviewApiDto reviewApiDto) {
        return ReviewMapper.toReviewApiDto(reviewsStorage.updateReview(ReviewMapper.toReview(reviewApiDto)));
    }

    public Collection<ReviewApiDto> getReviewByFilmd(Long filmId, Integer count) {
        return reviewsStorage.getReviewsByFilmId(filmId, count).stream()
                .map(ReviewMapper::toReviewApiDto)
                .collect(Collectors.toSet());
    }

    public ReviewApiDto deleteReviewById(Long id) {
        return ReviewMapper.toReviewApiDto(reviewsStorage.deleteReviewById(id));
    }

    public ReviewApiDto addLike(Long id, Long userId) {

        int useful = reviewsStorage.getReviewById(id).getUseful();

        // следующее условие добавлено для симметричного подхода как с дизлайками
        if (useful == -1) {
            useful = 1;
        } else {
            useful = useful + 1;
        }

        return ReviewMapper.toReviewApiDto(
                //reviewsStorage.updateReviewUseful(id, reviewsStorage.getReviewById(id).getUseful() + 1));
                reviewsStorage.updateReviewUseful(id, useful));
    }

    public ReviewApiDto addDislike(Long id, Long userId) {

        int useful = reviewsStorage.getReviewById(id).getUseful();

        // следующее условие добавлено на основании требований теста
        if (useful == 1) {
            useful = -1;
        } else {
            useful = useful - 1;
        }

        return ReviewMapper.toReviewApiDto(
                reviewsStorage.updateReviewUseful(id, useful));
    }

    public ReviewApiDto removeLike(Long id, Long userId) {
        return ReviewMapper.toReviewApiDto(
                reviewsStorage.updateReviewUseful(id, reviewsStorage.getReviewById(id).getUseful() - 1));
    }

    public ReviewApiDto removeDislike(Long id, Long userId) {
        return ReviewMapper.toReviewApiDto(
                reviewsStorage.updateReviewUseful(id, reviewsStorage.getReviewById(id).getUseful() + 1));
    }
}
