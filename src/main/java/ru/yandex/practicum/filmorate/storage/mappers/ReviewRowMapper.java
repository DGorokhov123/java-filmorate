package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReviewRowMapper implements RowMapper<Review> {

    public static final String CREATE_REVIEW_QUERY = """
            INSERT INTO REVIEWS (content, is_positive, user_id, film_id, useful)
            VALUES (?, ?, ?, ?, ?);
            """;


    public static final String GET_REVIEW_BY_ID_QUERY = """
            SELECT FROM REVIEWS (review_id, content, is_positive, user_id, film_id, useful)
            VALUES (?, ?, ?, ?, ?, ?);
            """;

    public static final String UPDATE_REVIEW_QUERY = "UPDATE REVIEWS " +
            "SET content = ?, is_positive = ?, user_id = ?, film_id = ?, useful = ?" +
            "WHERE review_id = ?;";

    public static final String DELETE_REVIEW_BY_ID_QUERY = "DELETE FROM REVIEWS WHERE review_id = ?;";


    public static final String GET_REVIEWS_BY_FILM_ID_QUERY = "SELECT " +
            "review_id, content, is_positive, user_id, film_id, useful" +
            "FROM REVIEWS WHERE film_id = ? LIMIT = ?;";


    public static final String GET_ALL_REVIEWS_QUERY = "SELECT " +
            "review_id, content, is_positive, user_id, film_id, useful" +
            "FROM REVIEWS LIMIT = ?;";

    public static final String UPDATE_REVIEW_USEFUL_QUERY = "UPDATE REVIEWS " +
            "useful = ?" +
            "WHERE review_id = ?;";

    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review();

        review.setReviewId(rs.getLong("REVIEW_ID"));
        review.setContent(rs.getString("CONTENT"));
        review.setIsPositive(rs.getBoolean("IS_POSITIVE"));
        review.setUserId(rs.getLong("USER_ID"));
        review.setFilmId(rs.getLong("FILM_ID"));
        review.setUseful(rs.getInt("USEFUL"));

        return review;
    }
}
