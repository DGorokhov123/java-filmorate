package ru.yandex.practicum.filmorate.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.DirectorApiDto;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@RestController
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    // Список всех режиссёров
    public Collection<DirectorApiDto> findAll() {
        return directorService.findAllDirector();
    }

    @GetMapping("/{id}")
    // Получение режиссёра по id
    public DirectorApiDto findById(@PathVariable("id") int id) {
        return directorService.findDirectorById(id);
    }

    @PostMapping
    // Создание режиссёра
    // TODO @Valid ?
    public DirectorApiDto create(@RequestBody DirectorApiDto directorApiDto) {
        return directorService.createDirector(directorApiDto);
    }

    @PutMapping
    // Изменение режиссёра
    // TODO @Valid ?
    public DirectorApiDto update(@RequestBody DirectorApiDto directorApiDto) {
        return directorService.updateDirector(directorApiDto);
    }

    @DeleteMapping("/{id}")
    // Удаление режиссёра
    public DirectorApiDto delete(@PathVariable("id") int id){
        return directorService.deleteDirector(id);
    }

}
