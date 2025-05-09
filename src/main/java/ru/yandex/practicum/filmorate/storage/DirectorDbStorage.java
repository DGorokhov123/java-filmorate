package ru.yandex.practicum.filmorate.storage;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.mappers.DirectorRowMapper;

import java.util.List;

@Slf4j
@Repository
public class DirectorDbStorage {

    private static final String FIND_ALL_DIRECTORS = "SELECT * FROM rating";
    private static final String FIND_BY_ID_DIRECTOR = "SELECT * FROM rating WHERE rating_id = ?";
    private static final String CREATE_DIRECTOR = "INSERT INTO directors (director_id, director_name) " +
            "VALUES (?, ?)";
    private static final String UPADTE_DIERECTOR = "UPDATE directors SET director_name = ? " +
            "WHERE director_id = ?";
    private static final String DELETE_DIRECTOR = "DELETE FROM directors WHERE director_id = ?";

    protected final JdbcTemplate jdbc;
    protected final DirectorRowMapper directorRowMapper;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbc, DirectorRowMapper drectorRowMapper) {
        this.jdbc = jdbc;
        this.directorRowMapper = drectorRowMapper;
    }

    // Список всех режиссёров
    public List<Director> findAllDirectors() {
        //TODO
        return null;
    }

    //  Получение режиссёра по id
    public Director findDirectorById(int id) {
        //TODO
        return null;
    }

    // Создание режиссёра
    public Director createDirector(Director director) {
        //TODO
        return null;
    }

    // Изменение режиссёра
    public Director updateDirector(Director director) {
        //TODO
        return null;
    }

    // Удаление режиссёра
    public Director deleteDirector(int id) {
        //TODO
        return null;
    }

}
