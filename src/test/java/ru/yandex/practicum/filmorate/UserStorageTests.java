package ru.yandex.practicum.filmorate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.NullEqualsException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

public class UserStorageTests {

    @Test
    void testCreateUser() {
        UserStorage userStorage = new InMemoryUserStorage();
        userStorage.createUser(User.builder()
                .login("JavaDev")
                .name("talented")
                .email("javadevs@email.com")
                .birthday(LocalDate.of(2000, 5, 22))
                .build());
        assertEquals(1, userStorage.getAllUsers().size(), "Ошибка добавления нового пользователя.");
        User user = userStorage.createUser(User.builder()
                .login("user2")
                .email("users2@mail.com")
                .build());
        assertEquals(user.getName(), user.getLogin(), "Ошибка задания имени по умолчанию.");
    }

    @Test
    void testUpdateUser() {
        UserStorage userStorage = new InMemoryUserStorage();
        assertThrows(NullEqualsException.class, () -> userStorage.updateUser(User.builder()
                .build()), "Некорректная проверка id на null.");
        assertThrows(NotFoundException.class, () -> userStorage.updateUser(User.builder()
                .id(1)
                .build()), "Ошибка проверки наличия пользователя.");
        User user1 = userStorage.createUser(User.builder()
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
        userStorage.updateUser(newUser1);
        assertEquals(userStorage.getAllUsers().getFirst(), newUser1, "Ошибка обновления данных пользователя.");
    }

    @Test
    void testGetAllUsers() {
        UserStorage userStorage = new InMemoryUserStorage();
        assertEquals(0, userStorage.getAllUsers().size(), "Ошибка получения всех пользователей.");
        userStorage.createUser(User.builder()
                .email("user1@mail.com")
                .name("user1")
                .login("user1")
                .birthday(LocalDate.now())
                .build());
        assertEquals(1, userStorage.getAllUsers().size(), "Ошибка получения всех пользователей.");
    }
}
