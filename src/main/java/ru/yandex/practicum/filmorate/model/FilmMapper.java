package ru.yandex.practicum.filmorate.model;

public class FilmMapper {

    public static FilmApiDto toDto(Film film) {
        FilmApiDto dto = new FilmApiDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setMpa(film.getMpa());
        dto.setGenres(film.getGenres());
        dto.setLikes(film.getLikes());
        return dto;
    }

    public static Film toFilm(FilmApiDto dto) {
        Film film = new Film();
        film.setId(dto.getId());
        film.setName(dto.getName());
        film.setDescription(dto.getDescription());
        film.setReleaseDate(dto.getReleaseDate());
        film.setDuration(dto.getDuration());
        film.setMpa(dto.getMpa());
        film.setGenres(dto.getGenres());
        film.setLikes(dto.getLikes());
        return film;
    }


}
