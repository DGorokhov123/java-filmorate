package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.exceptions.NotFoundException;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new ConcurrentHashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User getUserById(Long id) {
        if (id == null) throw new IllegalArgumentException("User id shouldn't be null");
        User user = users.get(id);
        if (user == null) throw new NotFoundException("User not found", id);
        return user;
    }

    @Override
    public User deleteUserById(Long id) {
        if (id == null) throw new IllegalArgumentException("User id shouldn't be null");
        User user = users.get(id);
        if (user == null) throw new NotFoundException("User not found", id);
        users.remove(id);
        return user;
    }

    @Override
    public User createUser(User user) {
        if (user == null) throw new IllegalArgumentException("User shouldn't be null");
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user == null) throw new IllegalArgumentException("User shouldn't be null");
        if (user.getId() == null) throw new IllegalArgumentException("User id shouldn't be null");
        User oldUser = users.get(user.getId());
        if (oldUser == null) throw new NotFoundException("User not found", user);
        oldUser.setEmail(user.getEmail());
        oldUser.setLogin(user.getLogin());
        oldUser.setName(user.getName());
        oldUser.setBirthday(user.getBirthday());
        return oldUser;
    }

    private Long getNextId() {
        return users.keySet().stream().mapToLong(id -> id).max().orElse(0) + 1;
    }

}
