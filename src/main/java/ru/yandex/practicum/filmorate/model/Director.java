package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
// Применение Jackson Library для десериализации объекта из JSON не совместимо с применением
// аннотаций  @Builder или @AllArgsConstructor, которые влияют на создание "пустого" контсруктора
public class Director {
    private Long id;
    private String name;

}
