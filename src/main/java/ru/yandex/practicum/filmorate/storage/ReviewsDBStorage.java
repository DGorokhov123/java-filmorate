package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewUserLike;
import ru.yandex.practicum.filmorate.model.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewUserLikeMapper;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ReviewsDBStorage {

    private final JdbcTemplate jdbc;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public Review createReview(Review newReview) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbc.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(ReviewRowMapper.CREATE_REVIEW_QUERY, new String[]{"REVIEW_ID"});
                ps.setString(1, newReview.getContent());
                ps.setBoolean(2, newReview.getIsPositive());
                ps.setLong(3, newReview.getUserId());
                ps.setLong(4, newReview.getFilmId());
                return ps;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            throw new ValidationException("Review duplicate key error", newReview);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Review referential integrity error", newReview);
        }
        newReview.setReviewId(keyHolder.getKey().longValue());

        return newReview;
    }


    public Review updateReview(Review reviewToUpdate) {

        checkAndReturnReviewById(reviewToUpdate.getReviewId());
        userStorage.checkUserById(reviewToUpdate.getUserId());
        filmStorage.checkFilmById(reviewToUpdate.getFilmId());


        try {
            jdbc.update(ReviewRowMapper.UPDATE_REVIEW_QUERY,
                    reviewToUpdate.getContent(),
                    reviewToUpdate.getIsPositive(),
                    reviewToUpdate.getUserId(),
                    reviewToUpdate.getFilmId(),
                    reviewToUpdate.getReviewId());
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Review referential integrity error", reviewToUpdate);
        }
        return reviewToUpdate;
    }

    public Review getReviewById(Long id) {
        return checkAndReturnReviewById(id);
    }


    public Review deleteReviewById(Long reviewId) {

        Review review = checkAndReturnReviewById(reviewId);
        jdbc.update(ReviewRowMapper.DELETE_REVIEW_BY_ID_QUERY, reviewId);

        return review;
    }

    public Review deleteReviewLikes(Long reviewId) {

        Review review = checkAndReturnReviewById(reviewId);
        jdbc.update(ReviewRowMapper.DELETE_REVIEW_REACTIONS_QUERY, reviewId);

        return review;
    }


    public Collection<Review> getReviewsByFilmId(Long filmId, int count) {

        filmStorage.checkFilmById(filmId);

        if (filmId == null || filmId == 0) {
            return jdbc.query(ReviewRowMapper.GET_ALL_REVIEWS_QUERY, new ReviewRowMapper(), count);
        }

        return jdbc.query(ReviewRowMapper.GET_REVIEWS_BY_FILM_ID_QUERY, new ReviewRowMapper(), filmId, count);
    }

    public Optional<ReviewUserLike> getUserReaction(Long reviewId, Long userId) {
        userStorage.checkUserById(userId);
        checkAndReturnReviewById(reviewId);

        try {
            return  Optional.ofNullable(
                    jdbc.queryForObject(ReviewRowMapper.GET_USER_REVIEW_REACTIONS_QUERY, new ReviewUserLikeMapper(), reviewId, userId)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Collection<ReviewUserLike> getReviewReactions(Long reviewId) {
        checkAndReturnReviewById(reviewId);
        return jdbc.query(ReviewRowMapper.GET_REVIEW_REACTIONS_QUERY, new ReviewUserLikeMapper(), reviewId);

    }


    public void addUserReaction(Long reviewId, Long userId, int userReaction) {
        userStorage.checkUserById(userId);
        checkAndReturnReviewById(reviewId);

        jdbc.update(ReviewRowMapper.CREATE_USER_REACTION_QUERY, reviewId, userId, userReaction);

    }



    public void deleteUserReaction(Long reviewId, Long userId) {
        userStorage.checkUserById(userId);
        checkAndReturnReviewById(reviewId);

        jdbc.update(ReviewRowMapper.DELETE_USER_REACTION_QUERY, reviewId , userId);

    }

    Review checkAndReturnReviewById(Long id) {
        try {
            return jdbc.queryForObject(ReviewRowMapper.GET_REVIEW_BY_ID_QUERY, new ReviewRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Review not found", id);
        }
    }
}
