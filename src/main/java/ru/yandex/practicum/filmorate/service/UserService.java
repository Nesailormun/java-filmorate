package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.NullEqualsException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        log.info("Обработка запроса на добавление нового пользователя.");
        checkName(user);
        userStorage.createUser(user);
        log.info("Пользователь с login = {} успешно создан.", user.getLogin());
        return user;
    }

    public User updateUser(User user) {
        log.info("Обработка запроса на обновление данных пользователя.");
        if (user.getId() == null) {
            log.error("Ошибка валидации, id = null, при обновлении данных пользователя.");
            throw new NullEqualsException("Id должен быть указан.");
        }
        if (userStorage.getUserById(user.getId()) == null) {
            log.error("Ошибка обновления, пользователя с id = {} не существует.", user.getId());
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден.");
        }
        User updatedUser = userStorage.getUserById(user.getId());
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
        userStorage.updateUser(updatedUser);
        log.info("Данные пользователя с login = {} успешно обновлены.", user.getLogin());
        return updatedUser;
    }

    public List<User> getAllUsers() {
        log.info("Обработка запроса на получение всех пользователей.");
        return userStorage.getAllUsers();
    }

    public User getUserById(Integer id) {
        log.info("Обработка запроса на получение данных пользователя.");
        User requiredUser = userStorage.getUserById(id);
        if (requiredUser == null) {
            log.error("Ошибка получения пользователя, пользователь с id = {} не найден.", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден.");
        }
        log.info("Запрос на получение данных пользователя успешно обработан.");
        return requiredUser;
    }

    public void deleteUserById(Integer id) {
        log.info("Обработка запроса на удаление пользователя с id = {}.", id);
        User requiredUser = userStorage.getUserById(id);
        if (requiredUser == null) {
            log.error("Ошибка удаления пользователя, пользователь с id = {} не найден.", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден.");
        }
        log.info("Пользователь с id = {} удален.", id);
        userStorage.deleteUserById(id);
    }

    public void addFriend(Integer userId, Integer friendId) {
        log.info("Обработка запроса на добавление пользователя в друзья.");
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user == null || friend == null) {
            log.error("Ошибка добавления друга, некорректно указан userId или friendId.");
            throw new NotFoundException("Ошибка, проверьте правильность ввода userId и friendId.");
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь c id = {} добавил в друзья пользователя с id = {}.", userId, friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        log.info("Обработка запроса на удаление пользователя из друзей.");
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user == null || friend == null) {
            log.error("Ошибка удаления друга, некорректно указан userId или friendId.");
            throw new NotFoundException("Ошибка, проверьте правильность ввода userId и friendId.");
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь c id = {} удалил из друзей пользователя с id = {}.", userId, friendId);
    }

    public List<User> getUsersFriends(Integer id) {
        log.info("Обработка запроса на получение списка друзей пользователя.");
        User user = userStorage.getUserById(id);
        if (user == null) {
            log.error("Ошибка получения списка друзей пользователя,  пользователь с id = {} не найден.", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден.");
        }
        log.info("Запрос на получение списка друзей пользователя с id = {} успешно обработан.", id);
        return user.getFriends()
                .stream()
                .map(userStorage::getUserById)
                .toList();
    }

    public List<User> getCommonUsersFriends(Integer userId, Integer otherId) {
        log.info("Обработка запроса на получение списка общих друзей.");
        User user = getUserById(userId);
        User otherUser = getUserById(otherId);
        if (user == null || otherUser == null) {
            log.error("Ошибка получения списка общих друзей, некорректно указан userId или otherId.");
            throw new NotFoundException("Ошибка, проверьте правильность ввода userId и otherId.");
        }
        log.info("Запрос на получение общего списка друзей пользователя с id = {} и пользователя с id = {} выполнен.",
                userId, otherId);
        return user.getFriends()
                .stream()
                .filter(otherUser.getFriends()::contains)
                .map(userStorage::getUserById)
                .toList();
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Установлено значение по умолчанию name = login = {}.",
                    user.getName());
        }
    }
}
