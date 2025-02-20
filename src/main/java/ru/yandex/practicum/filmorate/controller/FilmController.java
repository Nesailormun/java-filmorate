package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAllUsers() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не соответствует требованиям.");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Превышен лимит длины описания.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Некорректная дата релиза фильма.");
        }
        if (film.getDuration().toMinutes() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть больше нуля.");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if (film.getId() == 0) {
            throw new ValidationException("Id должен быть указан");
        }
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
        Film updatedFilm = films.get(film.getId());
        if (!(film.getName() == null))
            updatedFilm.setName(film.getName());
        if (!(film.getDescription() == null))
            updatedFilm.setDescription(film.getDescription());
        if (!(film.getReleaseDate() == null))
            updatedFilm.setReleaseDate(film.getReleaseDate());
        if (!(film.getDuration() == null))
            updatedFilm.setDuration(film.getDuration());
        films.put(film.getId(), updatedFilm);
        return updatedFilm;
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(1);
        return ++currentMaxId;
    }
}
