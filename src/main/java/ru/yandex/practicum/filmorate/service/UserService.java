package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserApiDto;
import ru.yandex.practicum.filmorate.model.UserMapper;
import ru.yandex.practicum.filmorate.service.validators.user.*;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
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


    public Collection<UserApiDto> getUsers() {
        return userStorage.getUsers().stream()
                .filter(Objects::nonNull)
                .map(UserMapper::toDto)
                .toList();
    }

    public UserApiDto getUserById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Invalid User id");
        User user = userStorage.getUserById(id);
        return UserMapper.toDto(user);
    }

    public UserApiDto deleteUserById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Invalid User id");
        User user = userStorage.deleteUserById(id);
        log.debug("Deleted user {}", user);
        return UserMapper.toDto(user);
    }

    public UserApiDto createUser(UserApiDto dto) {
        if (dto == null) throw new IllegalArgumentException("User object shouldn't be null");
        User user = UserMapper.toUser(dto);
        userCreateValidator.validate(user);
        User newUser = userStorage.createUser(user);
        log.debug("Created user {}", newUser);
        return UserMapper.toDto(newUser);
    }

    public UserApiDto updateUser(UserApiDto dto) {
        if (dto == null) throw new IllegalArgumentException("User object shouldn't be null");
        if (dto.getId() == null || dto.getId() < 1) throw new IllegalArgumentException("Invalid User id");
        User user = UserMapper.toUser(dto);
        userUpdateValidator.validate(user);
        User newUser = userStorage.updateUser(user);
        log.debug("Updated user {}", newUser);
        return UserMapper.toDto(newUser);
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

    public Set<UserApiDto> findFriends(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Invalid User id");
        User user = userStorage.getUserById(id);
        if (user.getFollowing().isEmpty()) return Set.of();
        Set<Long> friendIds = new HashSet<>(user.getFollowing());
        Collection<User> friendsCollection = userStorage.getUsersByIds(friendIds);
        return friendsCollection.stream()
                .filter(Objects::nonNull)
                .map(UserMapper::toDto)
                .collect(Collectors.toSet());
    }

    public Set<UserApiDto> findMutualFriends(Long id1, Long id2) {
        if (id1 == null || id2 == null || id1 < 1 || id2 < 1) throw new IllegalArgumentException("Invalid User id");
        if (Objects.equals(id1, id2)) throw new IllegalArgumentException("User ids are equal");
        Set<UserApiDto> mutualFriends = new HashSet<>(findFriends(id1));
        mutualFriends.retainAll(findFriends(id2));
        return mutualFriends;
    }

}
