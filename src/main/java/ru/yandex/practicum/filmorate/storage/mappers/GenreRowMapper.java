package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreRowMapper implements RowMapper<Genre> {

    public static String GET_GENRES_QUERY = """
            SELECT genre_id, name
            FROM genres;
            """;

    public static String GET_GENRE_BY_ID_QUERY = """
            SELECT genre_id, name
            FROM genres
            WHERE genre_id = ?;
            """;

    public static String GET_GENRE_BY_ID_CSV_QUERY = """
            SELECT genre_id, name
            FROM genres
            WHERE genre_id IN ( THE_LIST_OF_IDS );
            """;

    @Override
    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getLong("genre_id"));
        genre.setName(rs.getString("name"));
        return genre;
    }

}
