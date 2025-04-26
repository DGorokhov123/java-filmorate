package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.storage.RatingDBStorage;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingDBStorage ratingDBStorage;
    private Map<Long, Rating> ratingsCache = null;


    public Map<Long, Rating> getRatingsMap() {
        if (ratingsCache == null) updateRatingsCache();
        return ratingsCache;
    }

    public Collection<Rating> getRatings() {
        return getRatingsMap().values();
    }

    public Rating getRatingById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Invalid Rating Id");
        Rating rating = getRatingsMap().get(id);
        if (rating == null) throw new NotFoundException("Rating not found", id);
        return rating;
    }

    private void updateRatingsCache() {
        ratingsCache = new LinkedHashMap<>();
        List<Rating> ratingsFromDB = ratingDBStorage.getRatings();
        for (Rating rating : ratingsFromDB) ratingsCache.put(rating.getId(), rating);
    }


}
