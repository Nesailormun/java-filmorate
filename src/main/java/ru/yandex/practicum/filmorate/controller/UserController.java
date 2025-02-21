package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.NullEqualsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final ch.qos.logback.classic.Logger log = (ch.qos.logback.classic.Logger) LoggerFactory
            .getLogger(UserController.class);
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        log.info("Обработка запроса на добавление нового пользователя.");
        if (user == null) {
            log.warn("Ошибка добавления. Передан несуществующий объект. user = null.");
            throw new NullEqualsException("Ошибка, user = null.");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации email = {} при добавлении нового пользователя.", user.getEmail());
            throw new ValidationException("Email не соответствует требованиям.");
        }
        if (user.getLogin() == null || user.getLogin().contains(" ") || user.getLogin().isBlank()) {
            log.warn("Ошибка валидации login = {} при добавлении нового пользователя.", user.getLogin());
            throw new ValidationException("Имя пользователя не соответствует требованиям.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Установлено значение по умолчанию name = login = {} при добавлении нового пользователя.",
                    user.getName());
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации birthday = {} при добавлении нового пользователя.", user.getBirthday());
            throw new ValidationException("Некорректная дата рождения.");
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь с login = {} успешно создан.", user.getLogin());
        return user;
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        log.info("Обработка запроса на обновление данных пользователя.");
        if (user == null) {
            log.warn("Ошибка обновления. Передан несуществующий объект. user = null.");
            throw new NullEqualsException("Ошибка, user = null.");
        }
        if (user.getId() == null) {
            log.warn("Ошибка валидации, id = null, при обновлении данных пользователя.");
            throw new ValidationException("Id должен быть указан.");
        }
        if (!users.containsKey(user.getId())) {
            log.warn("Ошибка, пользователь с id = {} не найден.", user.getId());
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден.");
        }
        User updatedUser = users.get(user.getId());
        if (!(user.getEmail() == null)) {
            updatedUser.setEmail(user.getEmail());
            log.debug("Изменено значение поля email на: {}.", user.getEmail());
        }
        if (!(user.getName() == null)) {
            updatedUser.setName(user.getName());
            log.debug("Изменено значение поля name на: {}.", user.getName());
        }
        if (!(user.getLogin() == null)) {
            updatedUser.setLogin(user.getLogin());
            log.debug("Изменено значение поля login на: {}.", user.getLogin());
        }
        if (!(user.getBirthday() == null)) {
            updatedUser.setBirthday(user.getBirthday());
            log.debug("Изменено значение поля birthday на: {}.", user.getBirthday());
        }
        users.put(user.getId(), updatedUser);
        log.info("Данные пользователя с login = {} успешно обновлены.", user.getLogin());
        return updatedUser;
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

