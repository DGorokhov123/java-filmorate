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
import ru.yandex.practicum.filmorate.model.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewRowMapper;

import java.sql.PreparedStatement;
import java.util.Collection;

@RequiredArgsConstructor
@Repository
public class ReviewsDBStorage {

    private final JdbcTemplate jdbc;
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    public Review createReview(Review newReview) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbc.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(ReviewRowMapper.CREATE_REVIEW_QUERY, new String[]{"REVIEW_ID"});
                ps.setString(1, newReview.getContent());
                ps.setBoolean(2, newReview.getIsPositive());
                ps.setLong(3, newReview.getUserId());
                ps.setLong(4, newReview.getFilmId());

                if (newReview.getUseful() != null) {
                    ps.setInt(5, newReview.getUseful());
                } else {
                    ps.setInt(5, 0);
                }
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


    public Review deleteReviewById(Long reviewId) {

        Review review = checkAndReturnReviewById(reviewId);
        jdbc.update(ReviewRowMapper.DELETE_REVIEW_BY_ID_QUERY, reviewId);

        return review;
    }

    public Collection<Review> getReviewsByFilmId(Long filmId, int count) {

        filmStorage.checkFilmById(filmId);

        if (count == 0) {
            count = 10; // DEFAULT_MAX_NUMBER_OF_REVIEWS_OF_FILM
        }

        if (filmId == null || filmId == 0) {
            return jdbc.query(ReviewRowMapper.GET_ALL_REVIEWS_QUERY, new ReviewRowMapper(), count);
        }

        return jdbc.query(ReviewRowMapper.GET_REVIEWS_BY_FILM_ID_QUERY, new ReviewRowMapper(), filmId, count);
    }

    public Review updateReviewUseful(Long reviewId, Integer newUseful) {

        Review review = checkAndReturnReviewById(reviewId);

        jdbc.update(ReviewRowMapper.UPDATE_REVIEW_USEFUL_QUERY);

        return review;
    }


    Review checkAndReturnReviewById(Long id) {
        try {
            return jdbc.queryForObject(ReviewRowMapper.GET_REVIEW_BY_ID_QUERY, new ReviewRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Review not found", id);
        }
    }
}
