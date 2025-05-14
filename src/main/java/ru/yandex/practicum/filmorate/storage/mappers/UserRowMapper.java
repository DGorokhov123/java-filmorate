package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class UserRowMapper implements RowMapper<User> {

    public static String GET_USERS_QUERY = "SELECT " +
            "u.user_id AS id, u.email AS email, u.login AS login, u.name AS name, u.birthday AS birthday, " +
            "ARRAY_AGG(DISTINCT f1.friend_id) AS following, ARRAY_AGG(DISTINCT f2.user_id) AS followers " +
            "FROM users AS u " +
            "LEFT JOIN friends AS f1 ON u.user_id = f1.user_id " +
            "LEFT JOIN friends AS f2 ON u.user_id = f2.friend_id " +
            "GROUP BY u.user_id;";

    public static String GET_USER_BY_ID_QUERY = "SELECT " +
            "u.user_id AS id, u.email AS email, u.login AS login, u.name AS name, u.birthday AS birthday, " +
            "ARRAY_AGG(DISTINCT f1.friend_id) AS following, ARRAY_AGG(DISTINCT f2.user_id) AS followers " +
            "FROM users AS u " +
            "LEFT JOIN friends AS f1 ON u.user_id = f1.user_id " +
            "LEFT JOIN friends AS f2 ON u.user_id = f2.friend_id " +
            "WHERE u.user_id = ? " +
            "GROUP BY u.user_id;";

    public static String GET_USERS_BY_IDS_QUERY = "SELECT " +
            "u.user_id AS id, u.email AS email, u.login AS login, u.name AS name, u.birthday AS birthday, " +
            "ARRAY_AGG(DISTINCT f1.friend_id) AS following, ARRAY_AGG(DISTINCT f2.user_id) AS followers " +
            "FROM users AS u " +
            "LEFT JOIN friends AS f1 ON u.user_id = f1.user_id " +
            "LEFT JOIN friends AS f2 ON u.user_id = f2.friend_id " +
            "WHERE u.user_id IN (THE_LINE_OF_MASK) " +
            "GROUP BY u.user_id;";

    public static String GET_SIMPLE_USER_QUERY = "SELECT " +
            "u.user_id AS id, u.email AS email, u.login AS login, u.name AS name, u.birthday AS birthday, " +
            "NULL AS following, NULL AS followers " +
            "FROM users AS u WHERE u.user_id = ?;";

    public static String DELETE_USER_BY_ID_QUERY = "DELETE FROM users WHERE user_id = ?;";

    public static String CREATE_USER_QUERY = "INSERT INTO users (email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?);";

    public static String UPDATE_USER_QUERY = "UPDATE users " +
            "SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE user_id = ?;";

    public static String ADD_FRIEND_QUERY = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?);";

    public static String REMOVE_FRIEND_QUERY = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?;";

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        if (rs.getDate("birthday") != null) user.setBirthday(rs.getDate("birthday").toLocalDate());
        user.setFollowing(makeLongSet(rs.getArray("following")));
        user.setFollowers(makeLongSet(rs.getArray("followers")));
        return user;
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
