package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DirectorApiDto {
    //TODO проверка валидности?

    private int id;
    private String name;
}
