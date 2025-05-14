package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.storage.mappers.RatingRowMapper;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class RatingDBStorage {

    private final JdbcTemplate jdbc;

    public List<Rating> getRatings() {
        return jdbc.query(RatingRowMapper.GET_RATINGS_QUERY, new RatingRowMapper());
    }

    public Rating getRatingById(Long id) {
        try {
            return jdbc.queryForObject(RatingRowMapper.GET_RATING_BY_ID_QUERY, new RatingRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Rating not found", id);
        }
    }

}
