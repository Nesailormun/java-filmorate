package ru.yandex.practicum.filmorate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.NullEqualsException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserControllerTests {

    @Test
    void testCreateUser() {
        UserController userController = new UserController();
        userController.create(User.builder()
                .login("JavaDev")
                .name("talented")
                .email("javadevs@email.com")
                .birthday(LocalDate.of(2000, 5, 22))
                .build());
        assertEquals(1, userController.getAllUsers().size(), "Ошибка добавления нового пользователя.");
        User user = userController.create(User.builder()
                .login("user2")
                .email("users2@mail.com")
                .build());
        assertEquals(user.getName(), user.getLogin(), "Ошибка задания имени по умолчанию.");
    }

    @Test
    void testUpdateUser() {
        UserController userController = new UserController();
        assertThrows(NullEqualsException.class, () -> userController.update(User.builder()
                .build()), "Некорректная проверка id на null.");
        assertThrows(NotFoundException.class, () -> userController.update(User.builder()
                .id(1)
                .build()), "Ошибка проверки наличия пользователя.");
        User user1 = userController.create(User.builder()
                .email("user1@mail.com")
                .name("user1")
                .login("user1")
                .birthday(LocalDate.now())
                .build());
        User newUser1 = User.builder()
                .id(1)
                .email("newuser@mail.com")
                .name("newuser1")
                .login("newuser1")
                .birthday(LocalDate.of(2025, 1, 1))
                .build();
        userController.update(newUser1);
        assertEquals(userController.getAllUsers().getFirst(), newUser1, "Ошибка обновления данных пользователя.");
    }

    @Test
    void testGetAllUsers() {
        UserController userController = new UserController();
        assertEquals(0, userController.getAllUsers().size(), "Ошибка получения всех пользователей.");
        userController.create(User.builder()
                .email("user1@mail.com")
                .name("user1")
                .login("user1")
                .birthday(LocalDate.now())
                .build());
        assertEquals(1, userController.getAllUsers().size(), "Ошибка получения всех пользователей.");
    }
}
