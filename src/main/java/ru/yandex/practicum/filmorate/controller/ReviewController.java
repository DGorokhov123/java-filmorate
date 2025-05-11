package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.ReviewApiDto;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    //Добавление нового отзыва.
    @PostMapping
    public ReviewApiDto createReview(@Valid @RequestBody ReviewApiDto reviewDTO) {
        return reviewService.createReview(reviewDTO);
    }

    //Получение отзыва по идентификатору.
    @GetMapping("/{id}")
    public ReviewApiDto getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    //Редактирование уже имеющегося отзыва.
    @PutMapping
    public ReviewApiDto updateReview(@Valid @RequestBody ReviewApiDto reviewApiDto) {
        return reviewService.updateReview(reviewApiDto);
    }

    @GetMapping
    public Collection<ReviewApiDto> getReviewByFilmId(
            @RequestParam(required = false) Long filmId,
            @RequestParam(required = false, defaultValue = "10") Integer count) {
        return reviewService.getReviewByFilmId(filmId, count);
    }

    //Удаление уже имеющегося отзыва.
    @DeleteMapping("/{id}")
    public ReviewApiDto deleteReviewById(@PathVariable Long id) {
        return reviewService.deleteReviewById(id);
    }

    // Пользователь ставит лайк отзыву.
    @PutMapping("/{id}/like/{userId}")
    public ReviewApiDto addLike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.addLike(id, userId);
    }

    // Пользователь ставит дизлайк отзыву.
    @PutMapping("/{id}/dislike/{userId}")
    public ReviewApiDto addDislike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.addDislike(id, userId);
    }

    // Пользователь удаляет лайк отзыву.
    @DeleteMapping("/{id}/like/{userId}")
    public ReviewApiDto removeLike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.removeLike(id, userId);
    }

    // Пользователь удаляет дизлайк отзыву.
    @DeleteMapping("/{id}/dislike/{userId}")
    public ReviewApiDto removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.removeDislike(id, userId);
    }
}
