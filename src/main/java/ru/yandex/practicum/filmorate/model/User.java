package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {

    long id;

    @Email(message = "@Valid: User Email doesn't match email mask")
    String email;

    @NotBlank(message = "@Valid: User login shouldn't be blank")
    String login;

    String name;

    LocalDate birthday;

}
