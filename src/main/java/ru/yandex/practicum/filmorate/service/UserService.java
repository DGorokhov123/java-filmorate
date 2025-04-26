package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.validators.user.*;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserValidator userCreateValidator = UserValidatorBuilder.builder()
            .register(new UserNullValidator())
            .register(new UserEmailValidator())
            .register(new UserLoginValidator())
            .register(new UserNameValidator())
            .register(new UserBirthdayValidator())
            .build();

    private final UserValidator userUpdateValidator = UserValidatorBuilder.builder()
            .register(new UserNullValidator())
            .register(new UserIdValidator())
            .register(new UserEmailValidator())
            .register(new UserLoginValidator())
            .register(new UserNameValidator())
            .register(new UserBirthdayValidator())
            .build();

    private final UserStorage userStorage;


    // STORAGE OPERATIONS


    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Invalid User id");
        return userStorage.getUserById(id);
    }

    public User deleteUserById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Invalid User id");
        User user = userStorage.deleteUserById(id);
        log.debug("Deleted user {}", user);
        return user;
    }

    public User createUser(User user) {
        if (user == null) throw new IllegalArgumentException("User object shouldn't be null");
        userCreateValidator.validate(user);
        User newUser = userStorage.createUser(user);
        log.debug("Created user {}", newUser);
        return newUser;
    }

    public User updateUser(User user) {
        if (user == null) throw new IllegalArgumentException("User object shouldn't be null");
        if (user.getId() == null || user.getId() < 1) throw new IllegalArgumentException("Invalid User id");
        userUpdateValidator.validate(user);
        User newUser = userStorage.updateUser(user);
        log.debug("Updated user {}", newUser);
        return newUser;
    }


    // FRIENDS OPERATIONS


    public void addFriend(Long id1, Long id2) {
        if (id1 == null || id2 == null || id1 < 1 || id2 < 1) throw new IllegalArgumentException("Invalid User id");
        if (Objects.equals(id1, id2)) throw new IllegalArgumentException("User ids are equal");
        userStorage.addFriend(id1, id2);
        log.debug("User {} added friend {}", id1, id2);
    }

    public void removeFriend(Long id1, Long id2) {
        if (id1 == null || id2 == null || id1 < 1 || id2 < 1) throw new IllegalArgumentException("Invalid User id");
        if (Objects.equals(id1, id2)) throw new IllegalArgumentException("User ids are equal");
        userStorage.removeFriend(id1, id2);
        log.debug("User {} removed user {} from friends", id1, id2);
    }

    public Set<User> findFriends(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Invalid User id");
        User user = userStorage.getUserById(id);
        if (user.getFollowing().isEmpty()) return Set.of();
        Set<Long> friendIds = new HashSet<>(user.getFollowing());
        Collection<User> friendsCollection = userStorage.getUsersByIds(friendIds);
        return new HashSet<>(friendsCollection);
    }

    public Set<User> findMutualFriends(Long id1, Long id2) {
        if (id1 == null || id2 == null || id1 < 1 || id2 < 1) throw new IllegalArgumentException("Invalid User id");
        if (Objects.equals(id1, id2)) throw new IllegalArgumentException("User ids are equal");
        Set<User> mutualFriends = new HashSet<>(findFriends(id1));
        mutualFriends.retainAll(findFriends(id2));
        return mutualFriends;
    }

}
