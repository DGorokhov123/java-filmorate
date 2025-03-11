package ru.yandex.practicum.filmorate.model.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.validators.user.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    private User user;

    private final UserValidator userValidator = UserValidatorBuilder.builder()
            .register(new UserEmailValidator())
            .register(new UserLoginValidator())
            .register(new UserNameValidator())
            .register(new UserBirthdayValidator())
            .build();

    @BeforeEach
    void init() {
        user = new User();
        user.setId(3L);
        user.setEmail("ivan@ya.ru");
        user.setLogin("ivandur");
        user.setName("Ivan Durak");
        user.setBirthday(LocalDate.of(1991, 4, 1));
    }

    @Test
    void passesAllValidators() {
        assertDoesNotThrow(() -> {
            userValidator.validate(user);
        });
    }

    @Test
    void emailValidator() {
        user.setEmail("ivanya.ru");
        assertThrows(ValidationException.class, () -> {
            userValidator.validate(user);
        });

        user.setEmail("@ya.ru");
        assertThrows(ValidationException.class, () -> {
            userValidator.validate(user);
        });

        user.setEmail("");
        assertThrows(ValidationException.class, () -> {
            userValidator.validate(user);
        });

        user.setEmail(null);
        assertThrows(ValidationException.class, () -> {
            userValidator.validate(user);
        });
    }

    @Test
    void loginValidator() {
        user.setLogin("ivan durak");
        assertThrows(ValidationException.class, () -> {
            userValidator.validate(user);
        });

        user.setLogin("");
        assertThrows(ValidationException.class, () -> {
            userValidator.validate(user);
        });

        user.setLogin(null);
        assertThrows(ValidationException.class, () -> {
            userValidator.validate(user);
        });
    }

    @Test
    void birthdayValidator() {
        user.setBirthday(LocalDate.now().plusDays(10));
        assertThrows(ValidationException.class, () -> {
            userValidator.validate(user);
        });

        user.setBirthday(LocalDate.now());
        assertDoesNotThrow(() -> {
            userValidator.validate(user);
        });

        user.setBirthday(null);
        assertDoesNotThrow(() -> {
            userValidator.validate(user);
        });
    }

    @Test
    void nameValidator() {
        assertDoesNotThrow(() -> {
            userValidator.validate(user);
        });
        assertEquals("Ivan Durak", user.getName());

        user.setName("");
        assertDoesNotThrow(() -> {
            userValidator.validate(user);
        });
        assertEquals("ivandur", user.getName());

        user.setName(null);
        assertDoesNotThrow(() -> {
            userValidator.validate(user);
        });
        assertEquals("ivandur", user.getName());
    }


}