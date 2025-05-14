package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


@Data
@Builder
@AllArgsConstructor
public class DirectorApiDto {

    private Long id;

    @NotNull
    @NotBlank
    @NotEmpty
    @Length(max = 255)
    private String name;
}
