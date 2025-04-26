package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.Collection;
import java.util.Set;

@RequiredArgsConstructor
@Repository
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbc;

    @Override
    public Collection<User> getUsers() {
        return jdbc.query(UserRowMapper.GET_USERS_QUERY, new UserRowMapper());
    }

    @Override
    public User getUserById(Long id) {
        try {
            return jdbc.queryForObject(UserRowMapper.GET_USER_BY_ID_QUERY, new UserRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("User not found", id);
        }
    }

    @Override
    public Collection<User> getUsersByIds(Collection<Long> ids) {
        if (ids.isEmpty()) return Set.of();
        String idsLine = String.join(",", ids.stream().map(id -> "?").toList());
        String query = UserRowMapper.GET_USERS_BY_IDS_QUERY.replace("THE_LINE_OF_MASK", idsLine);
        return jdbc.query(query, new UserRowMapper(), ids.toArray());
    }

    @Override
    public void checkUserById(Long id) {
        try {
            jdbc.queryForObject(UserRowMapper.GET_SIMPLE_USER_QUERY, new UserRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("User not found", id);
        }
    }

    @Override
    public User deleteUserById(Long id) {
        User user;
        try {
            user = jdbc.queryForObject(UserRowMapper.GET_SIMPLE_USER_QUERY, new UserRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("User not found", id);
        }
        jdbc.update(UserRowMapper.DELETE_USER_BY_ID_QUERY, id);
        return user;
    }

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbc.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(UserRowMapper.CREATE_USER_QUERY, new String[]{"user_id"});
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getLogin());
                ps.setString(3, user.getName());
                if (user.getBirthday() != null) {
                    ps.setDate(4, Date.valueOf(user.getBirthday()));
                } else {
                    ps.setNull(4, Types.DATE);
                }
                return ps;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            throw new ValidationException("User duplicate key error", user);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("User referential integrity error", user);
        }
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        checkUserById(user.getId());
        try {
            jdbc.update(UserRowMapper.UPDATE_USER_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        } catch (DuplicateKeyException e) {
            throw new ValidationException("User duplicate key error", user);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("User referential integrity error", user);
        }
        return user;
    }

    @Override
    public void addFriend(Long id1, Long id2) {
        checkUserById(id1);
        checkUserById(id2);
        try {
            jdbc.update(UserRowMapper.ADD_FRIEND_QUERY, id1, id2);
        } catch (DuplicateKeyException e) {
            // do nothing
        }
    }

    @Override
    public void removeFriend(Long id1, Long id2) {
        checkUserById(id1);
        checkUserById(id2);
        jdbc.update(UserRowMapper.REMOVE_FRIEND_QUERY, id1, id2);
    }

}
