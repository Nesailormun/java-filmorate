package ru.yandex.practicum.filmorate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.NullEqualsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;

public class FilmStorageTests {

    @Test
    void testCreateFilm() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        filmStorage.createFilm(Film.builder()
                .name("Друзья")
                .description("Захватывающая история")
                .duration(30)
                .releaseDate(LocalDate.of(1999, 7, 1))
                .build());
        assertEquals(1, filmStorage.getAllFilms().size(), "Некорректное добавление фильма.");
        assertThrows(ValidationException.class, () -> filmStorage.createFilm(Film.builder()
                .name("Матильда")
                .releaseDate(LocalDate.of(1000, 9, 9))
                .build()), "Ошибка валидации даты");
    }

    @Test
    void testUpdateFilm() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        assertThrows(NullEqualsException.class, () -> filmStorage.updateFilm(Film.builder()
                .build()), "Ошибка проверки id фильма.");
        assertThrows(NotFoundException.class, () -> filmStorage.updateFilm(Film.builder()
                .id(2)
                .build()), "Ошибка проверки наличия фильма.");
        filmStorage.createFilm(Film.builder()
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
        filmStorage.updateFilm(newAvatar);
        assertEquals(filmStorage.getAllFilms().getFirst().getName(), newAvatar.getName(),
                "Ошибка обновления фильма");
    }

    @Test
    void testGetAllFilms() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        assertEquals(0, filmStorage.getAllFilms().size(), "Ошибка при получении списка фильмов");
        filmStorage.createFilm(Film.builder()
                .name("Avatar")
                .duration(180)
                .description("Exciting film")
                .releaseDate(LocalDate.of(2007, 8, 31))
                .build());
        assertEquals(1, filmStorage.getAllFilms().size(), "Ошибка при получении списка фильмов");
    }
}
