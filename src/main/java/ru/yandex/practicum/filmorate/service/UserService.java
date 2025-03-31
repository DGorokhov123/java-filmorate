package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.validators.user.*;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
        if (id == null) throw new IllegalArgumentException("User id shouldn't be null");
        return userStorage.getUserById(id);
    }

    public User deleteUserById(Long id) {
        if (id == null) throw new IllegalArgumentException("User id shouldn't be null");
        User user = userStorage.deleteUserById(id);
        log.debug("Deleted user {}", user);
        return user;
    }

    public User createUser(User user) {
        userCreateValidator.validate(user);
        User newUser = userStorage.createUser(user);
        log.debug("Created user {}", newUser);
        return newUser;
    }

    public User updateUser(User user) {
        userUpdateValidator.validate(user);
        User newUser = userStorage.updateUser(user);
        log.debug("Updated user {}", newUser);
        return newUser;
    }


    // FRIENDS OPERATIONS


    public void addFriend(Long id1, Long id2) {
        User user1 = userStorage.getUserById(id1);
        User user2 = userStorage.getUserById(id2);
        user1.getFriends().add(id2);
        user2.getFriends().add(id1);
        log.debug("Added friends: {} and {}", user1, user2);
    }

    public void removeFriend(Long id1, Long id2) {
        User user1 = userStorage.getUserById(id1);
        User user2 = userStorage.getUserById(id2);
        user1.getFriends().remove(id2);
        user2.getFriends().remove(id1);
        log.debug("Removed from friends: {} and {}", user1, user2);
    }

    public boolean areFriends(Long id1, Long id2) {
        User user1 = userStorage.getUserById(id1);
        return user1.getFriends().contains(id2);
    }

    public Set<User> findFriends(Long id) {
        Set<Long> friendIds = userStorage.getUserById(id).getFriends();
        Set<User> friendUsers = new HashSet<>();
        for (Long friendId : friendIds) {
            User userById = userStorage.getUserById(friendId);
            friendUsers.add(userById);
        }
        return friendUsers;
    }

    public Set<User> findMutualFriends(Long id1, Long id2) {
        Set<User> friends1 = findFriends(id1);
        Set<User> friends2 = findFriends(id2);
        return friends1.stream()
                .filter(friends2::contains)
                .collect(Collectors.toSet());
    }

}
