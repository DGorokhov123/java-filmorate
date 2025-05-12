package ru.yandex.practicum.filmorate.storage.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class FilmRowMapper implements RowMapper<Film> {

    /**
     * Рекомендации фильмов на основе коэффициента подобия Жаккара
     * временные таблицы:
     * target - хранит принимаемый параметр userId для использования в нескольких местах запроса
     * intersections - количество общих лайков по каждому пользователю, где они есть
     * likecounts - количество лайков каждого пользователя
     * targetlikecount - количество лайков целевого пользователя
     * neighbours - близкие "соседи" с максимальным коэффициентом подобия (в процентах)
     * итоговый запрос:
     * для каждого фильма, у которого есть лайки от "соседей" (за вычетом тех, что лайкнул целевой юзер)
     * расчитывается сумма коэффицентов подобия по всем соседским лайкам в итоговый балл с сортировкой
     *  + сразу же подгружаем данные для выгрузки фильмов в нужном для RowMapper формате
     */
    public static String GET_RECOMMENDED_FILMS_QUERY = """
            WITH target AS (
                SELECT CAST(? AS BIGINT) AS target_id
            ),
            intersections AS (
                SELECT l2.user_id, COUNT(*) AS intersection_count
                FROM likes AS l1
                JOIN likes AS l2 ON l1.film_id = l2.film_id AND l1.user_id != l2.user_id
                WHERE l1.user_id = (SELECT target_id FROM target)
                GROUP BY l2.user_id
            ),
            likecounts AS (
                SELECT user_id, COUNT(*) AS like_count FROM likes GROUP BY user_id
            ),
            targetlikecount AS (
                SELECT COUNT(*) AS target_like_count FROM LIKES WHERE user_id = (SELECT target_id FROM target)
            ),
            neighbours AS (
                SELECT
            	    i.user_id,
            	    i.intersection_count * 100 / ( t.target_like_count + lc.like_count - i.intersection_count ) AS similarity
                FROM intersections AS i
                JOIN likecounts lc ON i.user_id = lc.user_id
                CROSS JOIN targetlikecount AS t
                LIMIT 20
            )
            SELECT
            	f.film_id AS id,
            	f.name AS name,
                f.description AS description,
                f.release_date AS release_date,
                f.duration AS duration,
                f.rating_id AS rating_id,
                r.name AS rating_name,
                ARRAY_AGG(DISTINCT l2.user_id) AS likes,
                CAST(
                  JSON_ARRAYAGG(
                    DISTINCT JSON_OBJECT(
                      'id' : g.genre_id,
                      'name' : g.name
                    )
                  ) FILTER (WHERE g.genre_id IS NOT NULL) AS VARCHAR
                ) AS genres,
            	SUM(n.similarity) AS score
            FROM likes AS l
            JOIN neighbours AS n ON l.user_id = n.user_id
            LEFT JOIN films AS f ON f.film_id = l.film_id
            LEFT JOIN likes AS l2 ON f.film_id = l2.film_id
            LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
            LEFT JOIN genres AS g ON g.genre_id = fg.genre_id
            LEFT JOIN ratings AS r ON f.rating_id = r.rating_id
            WHERE l.film_id NOT IN (SELECT film_id FROM likes WHERE user_id = (SELECT target_id FROM target))
            GROUP BY l.film_id
            ORDER BY score DESC
            """;


    // ADD-MOST-POPULARS
    public static String GET_POPULAR_FILMS_QUERY = """
            SELECT
                f.film_id AS id,
                f.name AS name,
                f.description AS description,
                f.release_date AS release_date,
                f.duration AS duration,
                f.rating_id AS rating_id,
                r.name AS rating_name,
                ARRAY_AGG(DISTINCT l.user_id) AS likes,
                CAST(
                    JSON_ARRAYAGG(
                    DISTINCT JSON_OBJECT(
                    'id' : g.genre_id,
                    'name' : g.name
                                   )
                    ) FILTER (WHERE g.genre_id IS NOT NULL) AS VARCHAR
                    ) AS genres,
                CAST(
                     JSON_ARRAYAGG(
                     DISTINCT JSON_OBJECT(
                     'id' : d.director_id,
                     'name' : d.director_name
                                    )
                     ) FILTER (WHERE d.director_id IS NOT NULL) AS VARCHAR
                     ) AS directors
                FROM films AS f
                LEFT JOIN likes AS l ON f.film_id = l.film_id
                LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
                LEFT JOIN genres AS g ON g.genre_id = fg.genre_id
                LEFT JOIN ratings AS r ON f.rating_id = r.rating_id
                LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id
                LEFT JOIN directors AS d ON d.director_id = fd.director_id
            """;


    public static String GET_FILMS_QUERY = """
            SELECT
                f.film_id AS id,
                f.name AS name,
                f.description AS description,
                f.release_date AS release_date,
                f.duration AS duration,
                f.rating_id AS rating_id,
                r.name AS rating_name,
                ARRAY_AGG(DISTINCT l.user_id) AS likes,
                CAST(
                    JSON_ARRAYAGG(
                    DISTINCT JSON_OBJECT(
                    'id' : g.genre_id,
                    'name' : g.name
                                   )
                    ) FILTER (WHERE g.genre_id IS NOT NULL) AS VARCHAR
                    ) AS genres,
                CAST(
                     JSON_ARRAYAGG(
                     DISTINCT JSON_OBJECT(
                     'id' : d.director_id,
                     'name' : d.director_name
                                    )
                     ) FILTER (WHERE d.director_id IS NOT NULL) AS VARCHAR
                     ) AS directors
                FROM films AS f
                LEFT JOIN likes AS l ON f.film_id = l.film_id
                LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
                LEFT JOIN genres AS g ON g.genre_id = fg.genre_id
                LEFT JOIN ratings AS r ON f.rating_id = r.rating_id
                LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id
                LEFT JOIN directors AS d ON d.director_id = fd.director_id
            GROUP BY f.film_id;
            """;

    public static String GET_FILM_BY_ID_QUERY = """
                   SELECT
                f.film_id AS id,
                f.name AS name,
                f.description AS description,
                f.release_date AS release_date,
                f.duration AS duration,
                f.rating_id AS rating_id,
                r.name AS rating_name,
                ARRAY_AGG(DISTINCT l.user_id) AS likes,
                CAST(
                    JSON_ARRAYAGG(
                    DISTINCT JSON_OBJECT(
                    'id' : g.genre_id,
                    'name' : g.name
                                   )
                    ) FILTER (WHERE g.genre_id IS NOT NULL) AS VARCHAR
                    ) AS genres,
                CAST(
                     JSON_ARRAYAGG(
                     DISTINCT JSON_OBJECT(
                     'id' : d.director_id,
                     'name' : d.director_name
                                    )
                     ) FILTER (WHERE d.director_id IS NOT NULL) AS VARCHAR
                     ) AS directors
                FROM films AS f
                LEFT JOIN likes AS l ON f.film_id = l.film_id
                LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
                LEFT JOIN genres AS g ON g.genre_id = fg.genre_id
                LEFT JOIN ratings AS r ON f.rating_id = r.rating_id
                LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id
                LEFT JOIN directors AS d ON d.director_id = fd.director_id
            WHERE f.film_id = ?
            GROUP BY f.film_id;
            """;

    public static String GET_SIMPLE_FILM_QUERY = """
            SELECT
                f.film_id AS id,
                f.name AS name,
                f.description AS description,
                f.release_date AS release_date,
                f.duration AS duration,
                NULL AS rating_id,
                NULL AS rating_name,
                NULL AS likes,
                NULL AS genres,
                NULL AS directors
            FROM films AS f
            WHERE f.film_id = ?;
            """;

    public static String DELETE_FILM_BY_ID_QUERY = """
            DELETE FROM films
            WHERE film_id = ?;
            """;

    public static String CREATE_FILM_QUERY = """
            INSERT INTO films (name, description, release_date, duration, rating_id)
            VALUES (?, ?, ?, ?, ?);
            """;

    public static String UPDATE_FILM_QUERY = """
            UPDATE films
            SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?
            WHERE film_id = ?;
            """;

    public static String ADD_FILM_GENRE_QUERY = """
            INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?);
            """;

    public static String REMOVE_FILM_GENRES_QUERY = """
            DELETE FROM film_genres WHERE film_id = ?;
            """;

    public static String ADD_LIKE_QUERY = """
            INSERT INTO likes (film_id, user_id)
            VALUES (?, ?);
            """;

    public static String REMOVE_LIKE_QUERY = """
            DELETE FROM likes
            WHERE film_id = ? AND user_id = ?;
            """;


    // add-director feature
    public static String ADD_FILM_DIRECTOR_QUERY = """
            INSERT INTO film_directors (film_id, director_id) VALUES (?, ?);
            """;

    public static String REMOVE_FILM_DIRECTOR_QUERY = """
            DELETE FROM film_directors WHERE film_id = ?;
            """;

    public static String GET_FILMS_WITH_DIRECTORS_QUERY = """
            SELECT
                f.film_id AS id,
                f.name AS name,
                f.description AS description,
                f.release_date AS release_date,
                f.duration AS duration,
                f.rating_id AS rating_id,
                r.name AS rating_name,
                ARRAY_AGG(DISTINCT l.user_id) AS likes,
                CAST(
                    JSON_ARRAYAGG(
                    DISTINCT JSON_OBJECT(
                    'id' : g.genre_id,
                    'name' : g.name
                                   )
                    ) FILTER (WHERE g.genre_id IS NOT NULL) AS VARCHAR
                    ) AS genres,
                CAST(
                     JSON_ARRAYAGG(
                     DISTINCT JSON_OBJECT(
                     'id' : d.director_id,
                     'name' : d.director_name
                                    )
                     ) FILTER (WHERE d.director_id IS NOT NULL) AS VARCHAR
                     ) AS directors
                FROM films AS f
                LEFT JOIN likes AS l ON f.film_id = l.film_id
                LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
                LEFT JOIN genres AS g ON g.genre_id = fg.genre_id
                LEFT JOIN ratings AS r ON f.rating_id = r.rating_id
                LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id
                LEFT JOIN directors AS d ON d.director_id = fd.director_id
                WHERE d.director_id = ?
                GROUP BY f.film_id
            """;

    public static String SEARCH_FILMS_BY_TITLE_QUERY = """
            SELECT
                f.film_id AS id,
                f.name AS name,
                f.description AS description,
                f.release_date AS release_date,
                f.duration AS duration,
                f.rating_id AS rating_id,
                r.name AS rating_name,
                ARRAY_AGG(DISTINCT l.user_id) AS likes,
                CAST(
                        JSON_ARRAYAGG(
                            DISTINCT JSON_OBJECT(
                                'id' : g.genre_id,
                                'name' : g.name
                                               )
                                ) FILTER (WHERE g.genre_id IS NOT NULL) AS VARCHAR
                ) AS genres,
                CAST(
                        JSON_ARRAYAGG(
                            DISTINCT JSON_OBJECT(
                                 'id' : d.director_id,
                                 'name' : d.director_name
                                                )
                                 ) FILTER (WHERE d.director_id IS NOT NULL) AS VARCHAR
                ) AS directors
            FROM films AS f
                     LEFT JOIN likes AS l ON f.film_id = l.film_id
                     LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
                     LEFT JOIN genres AS g ON g.genre_id = fg.genre_id
                     LEFT JOIN ratings AS r ON f.rating_id = r.rating_id
                     LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id
                     LEFT JOIN directors AS d ON d.director_id = fd.director_id
            WHERE LOWER(f.name) LIKE CONCAT('%', ?, '%')
            GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name
            """;


    public static String SEARCH_FILMS_BY_DIRECTOR_QUERY = """
              SELECT
                f.film_id AS id,
                f.name AS name,
                f.description AS description,
                f.release_date AS release_date,
                f.duration AS duration,
                f.rating_id AS rating_id,
                r.name AS rating_name,
                ARRAY_AGG(DISTINCT l.user_id) AS likes,
                CAST(
                        JSON_ARRAYAGG(
                            DISTINCT JSON_OBJECT(
                                'id' : g.genre_id,
                                'name' : g.name
                                               )
                                ) FILTER (WHERE g.genre_id IS NOT NULL) AS VARCHAR
                ) AS genres,
                CAST(
                        JSON_ARRAYAGG(
                            DISTINCT JSON_OBJECT(
                                 'id' : d.director_id,
                                 'name' : d.director_name
                                                )
                                 ) FILTER (WHERE d.director_id IS NOT NULL) AS VARCHAR
                ) AS directors
            FROM films AS f
                     LEFT JOIN likes AS l ON f.film_id = l.film_id
                     LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
                     LEFT JOIN genres AS g ON g.genre_id = fg.genre_id
                     LEFT JOIN ratings AS r ON f.rating_id = r.rating_id
                     LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id
                     LEFT JOIN directors AS d ON d.director_id = fd.director_id
            WHERE LOWER(d.director_name) LIKE CONCAT('%', ?, '%')
            GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name
            """;

    public static String GET_FILMS_ID_BY_USER_ID_QUERY = "SELECT film_id FROM LIKES WHERE user_id = ?";

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));

        if (rs.getDate("release_date") != null) {
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        }

        Long dbDuration = rs.getObject("duration", Long.class);
        if (dbDuration != null) {
            film.setDuration(Duration.ofMillis(dbDuration));
        }

        Long dbRating = rs.getObject("rating_id", Long.class);
        if (dbRating != null) {
            Rating filmRating = new Rating();
            filmRating.setId(dbRating);
            filmRating.setName(rs.getString("rating_name"));
            film.setMpa(filmRating);
        }

        film.setLikes(makeLongSet(rs.getArray("likes")));

        String dbGenres = rs.getString("genres");
        if (dbGenres != null && !dbGenres.isBlank()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Set<Genre> filmGenres = objectMapper.readValue(dbGenres,
                        new TypeReference<Set<Genre>>() {
                        });
                film.setGenres(filmGenres);
            } catch (JsonProcessingException e) {
                // do nothing
            }

        }

        //add-directors feature
        String dbDirectors = rs.getString("directors");
        if (dbDirectors != null && !dbDirectors.isBlank()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Set<Director> filmDirectors = objectMapper.readValue(dbDirectors,
                        new TypeReference<Set<Director>>() {
                        });

                film.setDirectors(filmDirectors);
            } catch (JsonProcessingException e) {
                System.out.println(e.getMessage());
                // do nothing
            }

        }

        return film;
    }


    private Set<Long> makeLongSet(java.sql.Array sqlArray) throws SQLException {
        if (sqlArray == null) return new HashSet<>();
        Object[] objectArray = (Object[]) sqlArray.getArray();
        return Arrays.stream(objectArray)
                .filter(Objects::nonNull)
                .map(o -> ((Number) o).longValue())
                .collect(Collectors.toSet());
    }


}
