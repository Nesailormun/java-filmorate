package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@NotNull(message = "film не должен быть равен null.")
public class Film {

    private Integer id;
    @NotBlank(message = "Необходимо указать name.")
    private String name;
    @Size(max = 200, message = "Превышена максимальная длина описания.")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть больше нуля.")
    private Integer duration;
}
