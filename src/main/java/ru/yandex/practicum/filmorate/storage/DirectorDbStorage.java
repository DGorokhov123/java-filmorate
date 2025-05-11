package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.storage.mappers.DirectorRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Slf4j
@Repository
public class DirectorDbStorage {

    private static final String FIND_ALL_DIRECTORS = "SELECT * FROM directors";
    private static final String FIND_BY_ID_DIRECTOR = "SELECT * FROM directors WHERE director_id = ?";
    private static final String CREATE_DIRECTOR = "INSERT INTO directors (director_name) " +
            "VALUES (?)";
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
        log.trace("запрос на получения списка всех режисеров");
        return jdbc.query(FIND_ALL_DIRECTORS, directorRowMapper);
    }

    //  Получение режиссёра по id
    public Director findDirectorById(long id) {
        log.trace("запрос на получение объекта Режиссер с ID - {}", id);
        if (id < 0) {
            log.debug("при получении Режиссера не прошел валиидацию ID {}", id);
            throw new ValidationException("ID not valid", id);
        }
        try {
            Director director = jdbc.queryForObject(FIND_BY_ID_DIRECTOR, directorRowMapper, id);
            log.debug("найден Режиссер {}", director);
            return director;

        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Director not found ", id);
        }
    }

    // Создание режиссёра
    public Director createDirector(Director director) {
        log.trace("создание Режиссера {}", director);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(CREATE_DIRECTOR, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKeyAs(Long.class);
        // Возвращаем id нового пользователя
        if (id != null) {
            director.setId(id);
            log.debug("создан Режиссер {}", director);
            return director;
        } else {
            throw new RuntimeException("Director creation fail");
        }
    }

    // Изменение режиссёра
    public Director updateDirector(Director director) {
        log.trace("запрос на обновление Режиссера {}", director);
        if ((director.getId() == null) || (director.getId() < 0)) {
            log.debug("при обновлении не прошел валидацию Режиссер {}", director);
            throw new ValidationException("Director not valid", director);
        }
        int rowsUpdated = jdbc.update(UPADTE_DIERECTOR, director.getName(), director.getId());
        if (rowsUpdated == 0) {
            throw new NotFoundException("Director not found ", director);
        }
        log.debug("обновлен Режиссер {}", director);
        return director;
    }

    // Удаление режиссёра
    public Director deleteDirector(long id) {
        log.trace("запрос на удление Режиссера с ID - {}", id);
        // если директор не найдется по ID, будет NotFoundException
        Director director = findDirectorById(id);
        log.debug("запись с ID - {} будет удалена", id);
        int deletedRows = jdbc.update(DELETE_DIRECTOR, id);
        log.debug("удалено строк - {}", deletedRows);
        return director;
    }

}
