package ru.yandex.practicum.filmorate.model;

public class DirectorMapper {

    public static DirectorApiDto toApiDto(Director director) {
        return DirectorApiDto.builder()
                .id(director.getId())
                .name(director.getName())
                .build();
    }

    public static Director toDirector(DirectorApiDto directorApiDto) {
        Director director = new Director();
        director.setId(directorApiDto.getId());
        director.setName(directorApiDto.getName());
        return director;
    }

}
