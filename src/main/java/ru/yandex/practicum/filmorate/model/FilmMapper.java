package ru.yandex.practicum.filmorate.model;

import java.util.*;
import java.util.stream.Collectors;

public class FilmMapper {

    public static Film toFilm(FilmApiDto dto) {
        Film film = new Film();
        film.setId(dto.getId());
        film.setName(dto.getName());
        film.setDescription(dto.getDescription());
        film.setReleaseDate(dto.getReleaseDate());
        film.setDuration(dto.getDuration());
        film.setLikes(new HashSet<>(dto.getLikes()));
        if (dto.getMpa() != null) film.setRating(dto.getMpa().getId());
        Set<Long> genres = dto.getGenres().stream()
                .filter(Objects::nonNull)
                .map(Genre::getId)
                .collect(Collectors.toSet());
        film.setGenres(genres);
        return film;
    }

    public static FilmApiDto toDto(Film film, Map<Long, Rating> ratings, Map<Long, Genre> genres) {
        FilmApiDto dto = new FilmApiDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setLikes(new ArrayList<>(film.getLikes()));
        if (film.getRating() != null) dto.setMpa(ratings.get(film.getRating()));
        List<Genre> filmGenres = film.getGenres().stream()
                .map(genres::get)
                .filter(Objects::nonNull)
                .toList();
        dto.setGenres(filmGenres);
        return dto;
    }

}
