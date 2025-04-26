package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RatingRowMapper implements RowMapper<Rating> {

    public static String GET_RATINGS_QUERY = """
            SELECT rating_id, name
            FROM ratings;
            """;

    public static String GET_RATING_BY_ID_QUERY = """
            SELECT rating_id, name
            FROM ratings
            WHERE rating_id = ?;
            """;

    @Override
    public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
        Rating rating = new Rating();
        rating.setId(rs.getLong("rating_id"));
        rating.setName(rs.getString("name"));
        return rating;
    }

}
