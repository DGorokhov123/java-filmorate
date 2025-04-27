package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.storage.GenreDBStorage;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreDBStorage genreDBStorage;

    public Collection<Genre> getGenres() {
        return genreDBStorage.getGenres();
    }

    public Genre getGenreById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Invalid Genre Id");
        return genreDBStorage.getGenreById(id);
    }

    public List<Genre> getGenreByIdCSV(String idList) {
        if (idList == null || !idList.matches("[\\d,\\s]*"))
            throw new IllegalArgumentException("Invalid Genre Id list");
        return genreDBStorage.getGenreByIdCSV(idList);
    }

    public void checkFilmGenres(Film film) {
        if (film == null) throw new IllegalArgumentException("Invalid Null Film");
        if (film.getGenres() == null || film.getGenres().isEmpty()) return;
        String csv = film.getGenres().stream()
                .filter(Objects::nonNull)
                .map(Genre::getId)
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        List<Genre> dbGenres = getGenreByIdCSV(csv);

        Set<Long> filmGenreIds = film.getGenres().stream()
                .filter(Objects::nonNull)
                .map(Genre::getId)
                .collect(Collectors.toSet());

        Set<Long> dbGenreIds = dbGenres.stream()
                .filter(Objects::nonNull)
                .map(Genre::getId)
                .collect(Collectors.toSet());

        if (!Objects.equals(filmGenreIds, dbGenreIds)) {
            throw new NotFoundException("Genres not found", csv);
        }

    }


}
