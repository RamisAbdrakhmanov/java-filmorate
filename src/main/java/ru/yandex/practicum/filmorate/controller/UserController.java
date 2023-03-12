package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/users")
@Slf4j
@ResponseStatus(HttpStatus.OK)
public class UserController {
    private final InMemoryUserStorage userStorage;

    public UserController(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @GetMapping("/{id}")
    public User showUserById(@PathVariable int id) {
        return userStorage.showUserById(id);
    }


    @GetMapping
    public List<User> showUsers() {
        log.info("Запрос на вывод всех элеметов");
        return userStorage.showUsers();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Запросто на добавление элемента.");
        return userStorage.addUser(user);
    }

    @PutMapping
    public User changeUser(@Valid @RequestBody User user) {
        log.info("Запросто на изменения элемента.");
        return userStorage.changeUser(user);
    }

}
