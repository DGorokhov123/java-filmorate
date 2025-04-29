package ru.yandex.practicum.filmorate.model;

public class GenreMapper {

    public static GenreApiDto toDto(Genre genre) {
        GenreApiDto dto = new GenreApiDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }

    public static Genre toRating(GenreApiDto dto) {
        Genre genre = new Genre();
        genre.setId(dto.getId());
        genre.setName(dto.getName());
        return genre;
    }

}
