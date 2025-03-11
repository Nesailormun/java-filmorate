package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> getAllFilms();

    Film getFilmById(Integer id);

    void deleteFilmById(Integer id);

    Film updateFilm(Film film);

    Film createFilm(Film film);

}
