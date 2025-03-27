package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    private Long id;

    @Email(message = "@Valid: User Email doesn't match email mask")
    private String email;

    @NotBlank(message = "@Valid: User login shouldn't be blank")
    private String login;

    private String name;

    private LocalDate birthday;

    private final Set<Long> friends = new HashSet<>();

}
