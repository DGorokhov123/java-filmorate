package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private final UserStorage userStorage;
    private final UserService userService;

    UserServiceTest() {
        this.userStorage = new InMemoryUserStorage();
        this.userService = new UserService(userStorage);
    }

    @BeforeEach
    void setUp() {
        User u1 = new User();
        u1.setName("Dima");
        u1.setLogin("dimon");
        u1.setEmail("dima@ya.ru");
        userStorage.createUser(u1);

        User u2 = new User();
        u2.setName("Anna");
        u2.setLogin("anka");
        u2.setEmail("anka@ya.ru");
        userStorage.createUser(u2);

        User u3 = new User();
        u3.setName("Vika");
        u3.setLogin("vichka");
        u3.setEmail("victoria@ya.ru");
        userStorage.createUser(u3);
    }

    @Test
    void friendOps() {
        User u1 = userStorage.getUserById(1L);
        User u2 = userStorage.getUserById(2L);
        User u3 = userStorage.getUserById(3L);
        assertFalse(userService.areFriends(u1.getId(), u2.getId()));

        userService.addFriend(u1.getId(), u2.getId());
        assertTrue(userService.areFriends(u1.getId(), u2.getId()));

        assertFalse(userService.findFriends(u1.getId()).isEmpty());
        assertFalse(userService.findFriends(u2.getId()).isEmpty());
        assertTrue(userService.findFriends(u3.getId()).isEmpty());
        assertEquals(u2, userService.findFriends(u1.getId()).stream().findFirst().orElse(null));
        assertEquals(u1, userService.findFriends(u2.getId()).stream().findFirst().orElse(null));

        assertTrue(userService.findMutualFriends(u1.getId(), u2.getId()).isEmpty());
        userService.addFriend(u1.getId(), u3.getId());
        userService.addFriend(u3.getId(), u2.getId());
        assertFalse(userService.findMutualFriends(u1.getId(), u2.getId()).isEmpty());
        assertEquals(u3, userService.findMutualFriends(u1.getId(), u2.getId()).stream().findFirst().orElse(null));

        userService.removeFriend(u1.getId(), u3.getId());
        assertTrue(userService.findMutualFriends(u1.getId(), u2.getId()).isEmpty());
        assertFalse(userService.areFriends(u1.getId(), u3.getId()));
    }
}
