package ru.yandex.practicum.filmorate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.NullEqualsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmControllerTests {

    @Test
    void testCreateFilm() {
        FilmController filmController = new FilmController();
        assertThrows(NullEqualsException.class, () -> filmController.create(null),
                "Некорректная проверка на null.");
        filmController.create(Film.builder()
                .name("Друзья")
                .description("Захватывающая история")
                .duration(30)
                .releaseDate(LocalDate.of(1999, 7, 1))
                .build());
        assertEquals(1, filmController.getAllFilms().size(), "Некорректное добавление фильма.");
        assertThrows(ValidationException.class, () -> filmController.create(Film.builder()
                .name("")
                .releaseDate(LocalDate.of(1999, 9, 9))
                .build()), "Ошибка валидации имени");
        assertThrows(ValidationException.class, () -> filmController.create(Film.builder()
                .name("    ")
                .releaseDate(LocalDate.of(1999, 9, 9))
                .build()), "Ошибка валидации имени");
        assertThrows(ValidationException.class, () -> filmController.create(Film.builder()
                .name("Матильда")
                .releaseDate(LocalDate.of(1000, 9, 9))
                .build()), "Ошибка валидации даты");
        assertThrows(ValidationException.class, () -> filmController.create(Film.builder()
                .name("Титаник")
                .description("Великолепное кино, описание которого превышает 200 символов определенно" +
                        "---------__________________________________________________________________-" +
                        "______________________________________________________________________________--" +
                        "________________________________________________________________________________" +
                        "______________________________________________________________________________" +
                        "_____________________________________________________________________________" +
                        "___________________________________________________________________--" +
                        "______________________________________________________________________________" +
                        "____________________________________________________________________")
                .releaseDate(LocalDate.of(2000, 1, 20))
                .duration(180)
                .build()), "Ошибка валидации длины описания");
        assertEquals(1, filmController.getAllFilms().size(), "Некорректное добавление фильма.");
    }

    @Test
    void testUpdateFilm() {
        FilmController filmController = new FilmController();
        assertThrows(NullEqualsException.class, () -> filmController.update(null),
                "Некорректная проверка на null.");
        assertThrows(NullEqualsException.class, () -> filmController.update(Film.builder()
                .build()), "Ошибка проверки id фильма.");
        assertThrows(NotFoundException.class, () -> filmController.update(Film.builder()
                .id(2)
                .build()), "Ошибка проверки наличия фильма.");
        filmController.create(Film.builder()
                .name("Avatar")
                .duration(180)
                .description("Exciting film")
                .releaseDate(LocalDate.of(2007, 8, 31))
                .build());
        Film newAvatar = Film.builder()
                .id(1)
                .name("Avatar 1")
                .description("Amazing film Avatar 1")
                .build();
        filmController.update(newAvatar);
        assertEquals(filmController.getAllFilms().getFirst().getName(), newAvatar.getName(),
                "Ошибка обновления фильма");
    }

    @Test
    void testGetAllFilms() {
        FilmController filmController = new FilmController();
        assertEquals(0, filmController.getAllFilms().size(), "Ошибка при получении списка фильмов");
        filmController.create(Film.builder()
                .name("Avatar")
                .duration(180)
                .description("Exciting film")
                .releaseDate(LocalDate.of(2007, 8, 31))
                .build());
        assertEquals(1, filmController.getAllFilms().size(), "Ошибка при получении списка фильмов");
    }
}
