package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.exceptions.NotFoundException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
        User user = users.get(id);
        if (user == null) throw new NotFoundException("User not found", id);
        return user;
    }

    @Override
    public Collection<User> getUsersByIds(Collection<Long> ids) {
        Set<User> friendUsers = new HashSet<>();
        for (Long id : ids) {
            User userById = getUserById(id);
            friendUsers.add(userById);
        }
        return friendUsers;
    }

    @Override
    public void checkUserById(Long id) {
        if (!users.containsKey(id)) throw new NotFoundException("User not found", id);
    }

    @Override
    public User deleteUserById(Long id) {
        User user = users.get(id);
        if (user == null) throw new NotFoundException("User not found", id);
        users.remove(id);
        return user;
    }

    @Override
    public User createUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        User oldUser = users.get(user.getId());
        if (oldUser == null) throw new NotFoundException("User not found", user);
        oldUser.setEmail(user.getEmail());
        oldUser.setLogin(user.getLogin());
        oldUser.setName(user.getName());
        oldUser.setBirthday(user.getBirthday());
        return oldUser;
    }

    @Override
    public void addFriend(Long id1, Long id2) {
        User user1 = getUserById(id1);
        User user2 = getUserById(id2);
        user1.getFollowing().add(id2);
        user2.getFollowers().add(id1);
    }

    @Override
    public void removeFriend(Long id1, Long id2) {
        User user1 = getUserById(id1);
        User user2 = getUserById(id2);
        user1.getFollowing().remove(id2);
        user2.getFollowers().remove(id1);
    }

    private Long getNextId() {
        return users.keySet().stream().mapToLong(id -> id).max().orElse(0) + 1;
    }

}
