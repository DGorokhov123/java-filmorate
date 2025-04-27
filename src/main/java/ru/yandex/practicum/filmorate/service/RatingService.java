package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingDBStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingDBStorage ratingDBStorage;

    public Collection<Rating> getRatings() {
        return ratingDBStorage.getRatings();
    }

    public Rating getRatingById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Invalid Rating Id");
        return ratingDBStorage.getRatingById(id);
    }

    public void checkFilmRating(Film film) {
        if (film == null) throw new IllegalArgumentException("Invalid Null Film");
        Rating rating = film.getMpa();
        if (rating == null) return;
        ratingDBStorage.getRatingById(rating.getId());
    }

}
