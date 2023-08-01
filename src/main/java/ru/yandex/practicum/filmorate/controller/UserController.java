package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.notfound.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@ResponseStatus(HttpStatus.OK)
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        log.info("Запрос на вывод пользователя с id = {}", id);
        return userService.getUserById(id);
    }


    @GetMapping
    public List<User> getUsers() {
        log.info("Запрос на вывод всех пользователей");
        return userService.getUsers();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("запрос на добавление пользователя - {}", user);
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("запрос на изменение пользователя - {}", user);
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Integer id) {
        log.info("запрос на вывод удаление пользователя с id = {}", id);
        userService.deleteUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) {
        log.info("Запрос на получение друзей пользователя с id = {}", id);
        return userService.getFriends(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Запрос пользователя с id = {} на добавление пользователя с id = {} в друзья", id, friendId);
        if (id == friendId) {
            String message = "Пользователя не может добавить себя в друзья";
            log.info(message);
            throw new UserNotFoundException(message);
        }
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Запрос пользователя с id = {} на удаление пользователя с id = {} из друзей", id, friendId);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info("Запрос на получение списка общих друзей пользователя с id = {} и пользователя с id = {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendedFilms(@PathVariable Integer id) {
        log.info("Запрос на получение списка рекомендованных фильмов для пользователя с id = {}", id);
        return userService.getUsersRecommendations(id);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getFeed(@PathVariable Integer id) {
        log.info("Запрос на вывод ленты для пользователя с id = {}", id);
        return userService.getFeed(id);
    }
}
