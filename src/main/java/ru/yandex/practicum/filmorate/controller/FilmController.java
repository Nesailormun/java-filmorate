package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.NullEqualsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final ch.qos.logback.classic.Logger log = (ch.qos.logback.classic.Logger) LoggerFactory
            .getLogger(FilmController.class);
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Обработка запроса на добавление нового фильма.");
        validReleaseDate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм с названием: \"{}\" успешно добавлен в фильмотеку.", film.getName());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Обработка запроса на обновление данных о фильме.");
        if (film.getId() == null) {
            log.warn("Ошибка валидации id = null.");
            throw new NullEqualsException("Id должен быть указан.");
        }
        if (!films.containsKey(film.getId())) {
            log.warn("Фильм с id = {} не найден.", film.getId());
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден.");
        }
        validReleaseDate(film);
        Film updatedFilm = films.get(film.getId());
        if (film.getName() != null) {
            updatedFilm.setName(film.getName());
            log.debug("Изменено значение поля name на: {}", film.getName());
        }
        if (film.getDescription() != null) {
            updatedFilm.setDescription(film.getDescription());
            log.debug("Изменено значение поля description на: {}", film.getDescription());
        }
        if (film.getReleaseDate() != null) {
            updatedFilm.setReleaseDate(film.getReleaseDate());
            log.debug("Изменено значение поля releaseDate на: {}", film.getReleaseDate());
        }
        if (film.getDuration() != null) {
            updatedFilm.setDuration(film.getDuration());
            log.debug("Изменено значение поля duration на: {}", film.getDuration());
        }
        films.put(film.getId(), updatedFilm);
        log.info("Данные фильма с id = {} успешно обновлены.", film.getId());
        return updatedFilm;
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validReleaseDate(Film film) {
        if (film.getReleaseDate() != null &&
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Ошибка валидации releaseDate = {}", film.getReleaseDate());
            throw new ValidationException("Некорректная дата релиза фильма.");
        }
    }
}
