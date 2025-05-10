package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.ReviewUserLike;

import java.sql.ResultSet;
import java.sql.SQLException;


public class ReviewUserLikeMapper implements RowMapper<ReviewUserLike> {


    @Override
    public ReviewUserLike mapRow(ResultSet rs, int rowNum) throws SQLException {
        ReviewUserLike reviewUserLike = new ReviewUserLike();
        reviewUserLike.setReviewId(rs.getLong("REVIEW_ID"));
        reviewUserLike.setUserId(rs.getLong("USER_ID"));
        reviewUserLike.setReaction(rs.getInt("REACTION"));

        return reviewUserLike;
    }
}
