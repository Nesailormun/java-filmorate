package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@NotNull(message = "user не должен быть равен null.")
public class User {

    private Integer id;
    @NotBlank(message = "Необходимо указать email.")
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Неверный формат email.")
    private String email;
    @NotBlank(message = "Необходимо указать login.")
    @Pattern(regexp = "^\\S+$", message = "Неверный формат логина.")
    private String login;
    private String name;
    @PastOrPresent(message = "Некорректная дата рождения.")
    private LocalDate birthday;
}
