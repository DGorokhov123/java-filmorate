package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.storage.GenreDBStorage;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreDBStorage genreDBStorage;
    private Map<Long, Genre> genresCache = null;


    public Map<Long, Genre> getGenresMap() {
        if (genresCache == null) updateGenresCache();
        return genresCache;
    }


    public Collection<Genre> getGenres() {
        return getGenresMap().values();
    }

    public Genre getGenreById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Invalid Genre Id");
        Genre genre = getGenresMap().get(id);
        if (genre == null) throw new NotFoundException("Genre not found", id);
        return genre;
    }

    private void updateGenresCache() {
        genresCache = new LinkedHashMap<>();
        List<Genre> genresFromDB = genreDBStorage.getGenres();
        for (Genre genre : genresFromDB) genresCache.put(genre.getId(), genre);
    }


}
