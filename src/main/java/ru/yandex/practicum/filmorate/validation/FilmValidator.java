package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.constraints.Null;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NullEqualsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Component
@Slf4j
public class FilmValidator {

    public void validReleaseDate(Film film) {
        if (film.getReleaseDate() != null &&
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Ошибка валидации releaseDate = {}", film.getReleaseDate());
            throw new ValidationException("Некорректная дата релиза фильма.");
        }
    }

    public void validFilmsIdNotNull(Film film) {
        if (film.getId() == null) {
            log.error("Ошибка валидации id = null");
            throw new NullEqualsException("id должен быть указан.");
        }
    }
}
