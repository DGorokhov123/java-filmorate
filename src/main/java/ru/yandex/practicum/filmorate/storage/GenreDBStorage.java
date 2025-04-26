package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class GenreDBStorage {

    private final JdbcTemplate jdbc;

    public List<Genre> getGenres() {
        return jdbc.query(GenreRowMapper.GET_GENRES_QUERY, new GenreRowMapper());
    }

    public Genre getGenreById(Long id) {
        try {
            return jdbc.queryForObject(GenreRowMapper.GET_GENRE_BY_ID_QUERY, new GenreRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Genre not found", id);
        }
    }

}
