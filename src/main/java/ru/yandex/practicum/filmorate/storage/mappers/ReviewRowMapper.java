package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReviewRowMapper implements RowMapper<Review> {

    public static final String CREATE_REVIEW_QUERY =
            "INSERT INTO REVIEWS (content, is_positive, user_id, film_id) VALUES (?, ?, ?, ?);";

    public static final String GET_REVIEW_BY_ID_QUERY =
            "SELECT  review_id, content, is_positive, user_id, film_id FROM REVIEWS WHERE REVIEW_ID = ?;";

    public static final String UPDATE_REVIEW_QUERY = "UPDATE REVIEWS " +
            "SET content = ?, is_positive = ?, user_id = ?, film_id = ? " +
            "WHERE review_id = ?;";

    public static final String DELETE_REVIEW_BY_ID_QUERY = "DELETE FROM REVIEWS WHERE review_id = ?;";

    public static final String GET_REVIEWS_BY_FILM_ID_QUERY = "SELECT " +
            "review_id, content, is_positive, user_id, film_id " +
            "FROM REVIEWS WHERE film_id = ? LIMIT ? ;";

    public static final String GET_ALL_REVIEWS_QUERY = "SELECT " +
            "review_id, content, is_positive, user_id, film_id " +
            "FROM REVIEWS LIMIT = ?;";

    public static final String CREATE_USER_REACTION_QUERY =
            "INSERT INTO REVIEWS_REACTIONS (review_id, user_id, reaction) VALUES (?, ?, ?);";

    public static final String DELETE_REVIEW_REACTIONS_QUERY = "DELETE FROM REVIEWS_REACTIONS WHERE review_id = ?;";

    public static final String DELETE_USER_REACTION_QUERY =
            "DELETE FROM REVIEWS_REACTIONS WHERE review_id = ? AND user_id = ?;";

    public static String GET_USER_REVIEW_REACTIONS_QUERY =
            "SELECT  review_id,  user_id, reaction FROM REVIEWS_REACTIONS WHERE REVIEW_ID = ? AND USER_ID = ?;";

    public static String GET_REVIEW_REACTIONS_QUERY =
            "SELECT  review_id,  user_id, reaction FROM REVIEWS_REACTIONS WHERE REVIEW_ID = ?;";

    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getLong("REVIEW_ID"))
                .content(rs.getString("CONTENT"))
                .isPositive(rs.getBoolean("IS_POSITIVE"))
                .userId(rs.getLong("USER_ID"))
                .filmId(rs.getLong("FILM_ID"))
                .build();
    }
}
