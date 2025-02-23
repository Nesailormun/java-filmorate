package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Level;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.NullEqualsException;
import ru.yandex.practicum.filmorate.model.User;

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
        checkName(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь с login = {} успешно создан.", user.getLogin());
        return user;
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        log.info("Обработка запроса на обновление данных пользователя.");
        if (user.getId() == null) {
            log.warn("Ошибка валидации, id = null, при обновлении данных пользователя.");
            throw new NullEqualsException("Id должен быть указан.");
        }
        if (!users.containsKey(user.getId())) {
            log.warn("Ошибка, пользователь с id = {} не найден.", user.getId());
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден.");
        }
        User updatedUser = users.get(user.getId());
        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
            log.debug("Изменено значение поля email на: {}.", user.getEmail());
        }
        if (user.getLogin() != null) {
            updatedUser.setLogin(user.getLogin());
            log.debug("Изменено значение поля login на: {}.", user.getLogin());
        }
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
            log.debug("Изменено значение поля name на: {}.", user.getName());
        } else if (updatedUser.getName().equals(updatedUser.getLogin())) {
            checkName(user);
            updatedUser.setName(user.getName());
        }
        if (user.getBirthday() != null) {
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

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Установлено значение по умолчанию name = login = {}.",
                    user.getName());
        }
    }

    public static void main(String[] args) {
        log.setLevel(Level.DEBUG);
        UserController userController = new UserController();
        User oldUser = User.builder()
                .name("oldUserName")
                .email("oldUser@mail.com")
                .login("oldUser")
                .build();
        User newUser = User.builder()
                .id(1)
                .login("newUser")
                .email("newUser@mail.com").build();
        userController.create(oldUser);
        userController.update(newUser);
        System.out.println(userController.getAllUsers());
    }
}

