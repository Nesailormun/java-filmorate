package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmValidator filmValidator;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, FilmValidator filmValidator) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmValidator = filmValidator;
    }

    public Film createFilm(Film film) {
        log.info("Обработка запроса на добавление нового фильма.");
        filmValidator.validReleaseDate(film);
        filmStorage.createFilm(film);
        log.info("Фильм с названием: \"{}\" успешно добавлен в фильмотеку.", film.getName());
        return film;
    }

    public Film updateFilm(Film film) {
        log.info("Обработка запроса на обновление данных о фильме.");
        filmValidator.validFilmsIdNotNull(film);
        if (filmStorage.getFilmById(film.getId()) == null) {
            log.error("Фильм с id = {} не найден.", film.getId());
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден.");
        }
        filmValidator.validReleaseDate(film);
        Film updatedFilm = filmStorage.getFilmById(film.getId());
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
        filmStorage.updateFilm(updatedFilm);
        log.info("Данные фильма с id = {} успешно обновлены.", film.getId());
        return updatedFilm;
    }

    public void deleteFilmById(Integer id) {
        log.info("Обработка запроса на удаление фильма.");
        if (filmStorage.getFilmById(id) == null) {
            log.error("Фильм с id = {} не существует.", id);
            throw new NotFoundException("Фильм с id = " + id + " не существует.");
        }
        log.info("Запрос на удаление фильма с id = {} успешно обработан.", id);
        filmStorage.deleteFilmById(id);
    }

    public List<Film> getAllFilms() {
        log.info("Обработка запроса на получение списка всех фильмов.");
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Integer id) {
        log.info("Обработка запроса на получение фильма по id.");
        if (filmStorage.getFilmById(id) == null) {
            log.error("Ошибка получения фильма фильм с id = {} не найден.", id);
            throw new NotFoundException("Фильм с id = " + id + " не найден.");
        }
        log.info("Запрос на получение пользователя с id = {} успешно обработан.", id);
        return filmStorage.getFilmById(id);
    }

    public void addLike(Integer filmId, Integer userId) {
        log.info("Обработка запроса на добавление лайка к фильму.");
        if (filmStorage.getFilmById(filmId) == null || userStorage.getUserById(userId) == null) {
            log.error("Ошибка добавления лайка к фильму, некорректные значения filmId или userId.");
            throw new NotFoundException("Ошибка, проверьте правильность ввода filmId и userId.");
        }
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().add(userId);
        log.info("Запрос на добавление лайка к фильму с filmId = {} пользователем c userId = {} обработан.",
                filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        log.info("Обработка запроса на удаление лайка к фильму.");
        if (filmStorage.getFilmById(filmId) == null || userStorage.getUserById(userId) == null) {
            log.error("Ошибка удаления лайка к фильму, некорректные значения filmId или userId.");
            throw new NotFoundException("Ошибка, проверьте правильность ввода filmId и userId.");
        }
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().remove(userId);
        log.info("Запрос на удаление лайка к фильму с filmId = {} пользователем c userId = {} обработан.",
                filmId, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getAllFilms()
                .stream()
                .sorted(Comparator.comparingInt(film -> film.getLikes().size()))
                .toList()
                .reversed()
                .stream()
                .limit(count)
                .toList();
    }
}
