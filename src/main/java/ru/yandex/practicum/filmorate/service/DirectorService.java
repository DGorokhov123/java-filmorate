package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.DirectorApiDto;
import ru.yandex.practicum.filmorate.model.DirectorMapper;
import ru.yandex.practicum.filmorate.storage.DirectorDbStorage;

import java.util.Collection;

@Service
public class DirectorService {

    private final DirectorDbStorage directorDbStorage;

    @Autowired
    public DirectorService(DirectorDbStorage directorDbStorage) {
        this.directorDbStorage = directorDbStorage;
    }

    public Collection<DirectorApiDto> findAllDirector() {
        return directorDbStorage.findAllDirectors().stream()
                .map(DirectorMapper::toApiDto)
                .toList();
    }

    public DirectorApiDto findDirectorById(long id) {
        return DirectorMapper.toApiDto(directorDbStorage.findDirectorById(id));
    }

    public DirectorApiDto createDirector(DirectorApiDto directorApiDto) {
        return DirectorMapper.toApiDto(directorDbStorage.createDirector(
                DirectorMapper.toDirector(directorApiDto)));
    }

    public DirectorApiDto updateDirector(DirectorApiDto directorApiDto) {
        return DirectorMapper.toApiDto(directorDbStorage.updateDirector(
                DirectorMapper.toDirector(directorApiDto)));
    }

    public DirectorApiDto deleteDirector(long id) {
        return  DirectorMapper.toApiDto(directorDbStorage.deleteDirector(id));
    }

}
