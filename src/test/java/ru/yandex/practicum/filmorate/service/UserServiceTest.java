package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.UserApiDto;
import ru.yandex.practicum.filmorate.model.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserService.class, UserDbStorage.class})
class UserServiceTest {

    private final UserService userService;

    @BeforeEach
    void setUp() {
        UserApiDto u1 = new UserApiDto();
        u1.setName("Dima");
        u1.setLogin("dimon");
        u1.setEmail("dima@ya.ru");
        userService.createUser(u1);

        UserApiDto u2 = new UserApiDto();
        u2.setName("Anna");
        u2.setLogin("anka");
        u2.setEmail("anka@ya.ru");
        userService.createUser(u2);

        UserApiDto u3 = new UserApiDto();
        u3.setName("Vika");
        u3.setLogin("vichka");
        u3.setEmail("victoria@ya.ru");
        userService.createUser(u3);
    }

    @Test
    void friendOps() {
        assertTrue(userService.findFriends(1L).isEmpty());
        assertTrue(userService.findFriends(2L).isEmpty());
        assertTrue(userService.findFriends(3L).isEmpty());

        userService.addFriend(1L, 2L);
        assertTrue(userService.findFriends(1L).stream().filter(Objects::nonNull).map(UserApiDto::getId).anyMatch(l -> l == 2L));
        assertTrue(userService.findFriends(2L).isEmpty());
        assertTrue(userService.findFriends(3L).isEmpty());

        userService.addFriend(2L, 1L);
        assertTrue(userService.findFriends(1L).stream().filter(Objects::nonNull).map(UserApiDto::getId).anyMatch(l -> l == 2L));
        assertTrue(userService.findFriends(2L).stream().filter(Objects::nonNull).map(UserApiDto::getId).anyMatch(l -> l == 1L));
        assertTrue(userService.findFriends(3L).isEmpty());

        assertTrue(userService.findMutualFriends(1L, 2L).isEmpty());
        assertTrue(userService.findMutualFriends(2L, 1L).isEmpty());
        assertTrue(userService.findMutualFriends(1L, 3L).isEmpty());
        assertTrue(userService.findMutualFriends(3L, 1L).isEmpty());
        assertTrue(userService.findMutualFriends(3L, 2L).isEmpty());
        assertTrue(userService.findMutualFriends(2L, 3L).isEmpty());

        userService.addFriend(3L, 2L);
        assertTrue(userService.findMutualFriends(1L, 2L).isEmpty());
        assertTrue(userService.findMutualFriends(2L, 1L).isEmpty());
        assertTrue(userService.findMutualFriends(1L, 3L).stream().filter(Objects::nonNull).map(UserApiDto::getId).anyMatch(l -> l == 2L));
        assertTrue(userService.findMutualFriends(3L, 1L).stream().filter(Objects::nonNull).map(UserApiDto::getId).anyMatch(l -> l == 2L));
        assertTrue(userService.findMutualFriends(3L, 2L).isEmpty());
        assertTrue(userService.findMutualFriends(2L, 3L).isEmpty());

        userService.removeFriend(1L, 2L);
        userService.removeFriend(2L, 1L);
        userService.removeFriend(3L, 2L);
        assertTrue(userService.findFriends(1L).isEmpty());
        assertTrue(userService.findFriends(2L).isEmpty());
        assertTrue(userService.findFriends(3L).isEmpty());

        // Wrong operations

        assertThrows(NotFoundException.class, () -> {
            userService.addFriend(1L, 2000L);
        });
        assertThrows(NotFoundException.class, () -> {
            userService.addFriend(1000L, 2L);
        });
        assertThrows(NotFoundException.class, () -> {
            userService.removeFriend(1L, 2000L);
        });
        assertThrows(NotFoundException.class, () -> {
            userService.removeFriend(1000L, 2L);
        });

        assertThrows(NotFoundException.class, () -> {
            userService.findFriends(2000L);
        });
        assertThrows(NotFoundException.class, () -> {
            userService.findMutualFriends(1000L, 2L);
        });
        assertThrows(NotFoundException.class, () -> {
            userService.findMutualFriends(1L, 2000L);
        });

    }


    @Test
    void getUsers() {
        Collection<UserApiDto> users = userService.getUsers();
        List<String> logins = users.stream().filter(Objects::nonNull).map(UserApiDto::getLogin).toList();
        assertEquals(3, users.size());
        assertTrue(logins.contains("dimon"));
        assertTrue(logins.contains("anka"));
        assertTrue(logins.contains("vichka"));
    }

    @Test
    void userCrudOps() {
        UserApiDto user = new UserApiDto();
        user.setEmail("potus46@usa.gov");
        user.setLogin("biden");
        user.setName("Joe");
        user.setBirthday(LocalDate.of(1942, 11, 20));

        UserApiDto createdUser = userService.createUser(user);
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getLogin(), createdUser.getLogin());
        assertEquals(user.getName(), createdUser.getName());
        assertEquals(user.getBirthday(), createdUser.getBirthday());

        Long id = createdUser.getId();

        UserApiDto nextUser = new UserApiDto();
        nextUser.setId(id);
        nextUser.setEmail("potus47@usa.gov");
        nextUser.setLogin("trump");
        nextUser.setName("Donald");
        nextUser.setBirthday(LocalDate.of(1946, 6, 14));

        UserApiDto updatedUser = userService.updateUser(nextUser);
        assertEquals(nextUser.getId(), updatedUser.getId());
        assertEquals(nextUser.getEmail(), updatedUser.getEmail());
        assertEquals(nextUser.getLogin(), updatedUser.getLogin());
        assertEquals(nextUser.getName(), updatedUser.getName());
        assertEquals(nextUser.getBirthday(), updatedUser.getBirthday());

        UserApiDto receivedUser = userService.getUserById(id);
        assertEquals(nextUser.getId(), receivedUser.getId());
        assertEquals(nextUser.getEmail(), receivedUser.getEmail());
        assertEquals(nextUser.getLogin(), receivedUser.getLogin());
        assertEquals(nextUser.getName(), receivedUser.getName());
        assertEquals(nextUser.getBirthday(), receivedUser.getBirthday());

        userService.deleteUserById(id);
        assertThrows(NotFoundException.class, () -> {
            userService.getUserById(id);
        });

        // Wrong operations

        user = new UserApiDto();
        user.setEmail("bad@guy.am");
        user.setLogin("malo");
        user.setName("baka");
        user.setBirthday(LocalDate.of(2942, 11, 20));               // wrong birthdate
        UserApiDto finalUser = user;
        assertThrows(ValidationException.class, () -> {
            userService.createUser(finalUser);
        });

        user = new UserApiDto();
        user.setEmail("badguy.am");                //  wrong email
        user.setLogin("malo");
        user.setName("baka");
        user.setBirthday(LocalDate.of(1942, 11, 20));
        UserApiDto finalUser1 = user;
        assertThrows(ValidationException.class, () -> {
            userService.createUser(finalUser1);
        });

        user = new UserApiDto();
        user.setEmail("bad@guy.am");
        user.setLogin("muy malo");               // wrong login
        user.setName("baka");
        user.setBirthday(LocalDate.of(1942, 11, 20));
        UserApiDto finalUser2 = user;
        assertThrows(ValidationException.class, () -> {
            userService.createUser(finalUser2);
        });

        user = new UserApiDto();
        user.setEmail("dima@ya.ru");       // duplicate email
        user.setLogin("malo");
        user.setName("baka");
        user.setBirthday(LocalDate.of(1942, 11, 20));
        UserApiDto finalUser3 = user;
        assertThrows(ValidationException.class, () -> {
            userService.createUser(finalUser3);
        });


        assertThrows(NotFoundException.class, () -> {
            userService.getUserById(3000L);                     // unknown id
        });


        user = new UserApiDto();                       // update without id
        user.setEmail("bad@guy.am");
        user.setLogin("malo");
        user.setName("baka");
        user.setBirthday(LocalDate.of(1942, 11, 20));
        UserApiDto finalUser4 = user;
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(finalUser4);
        });

        user = new UserApiDto();
        user.setId(2L);
        user.setEmail("dima@ya.ru");            // update duplicate email
        user.setLogin("malo");
        user.setName("baka");
        user.setBirthday(LocalDate.of(1942, 11, 20));
        UserApiDto finalUser5 = user;
        assertThrows(ValidationException.class, () -> {
            userService.updateUser(finalUser5);
        });

        user = new UserApiDto();
        user.setId(2000L);                   // update with wrong id
        user.setEmail("bad@guy.am");
        user.setLogin("malo");
        user.setName("baka");
        user.setBirthday(LocalDate.of(1942, 11, 20));
        UserApiDto finalUser6 = user;
        assertThrows(NotFoundException.class, () -> {
            userService.updateUser(finalUser6);
        });


        assertThrows(NotFoundException.class, () -> {
            userService.deleteUserById(3000L);                     // unknown id
        });

    }

}
