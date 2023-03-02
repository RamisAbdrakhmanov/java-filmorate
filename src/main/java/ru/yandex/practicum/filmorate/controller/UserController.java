package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;


@RestController
@RequestMapping("/users")
@Slf4j
@ResponseStatus(HttpStatus.OK)
public class UserController {
    private final Set<User> users = new HashSet<>();
    public static int idCount = 1;

    @GetMapping
    public Set<User> showUsers() {
        log.info("Список users содержит - {} элементов", users.size());
        return users;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Запросто на добавление элемента.");
        if (user.getName() == null || user.getName().equals("")) {
            log.debug("Отсутствует имя пользователя {}", user);
            user.setName(user.getLogin());
        }
        if (user.getId() == 0) {
            user.setId(idCount++);
        }

        users.add(user);
        return user;
    }

    @PutMapping
    public User changeUser(@Valid @RequestBody User user) {

        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        for (User userSet : users) {
            if (userSet.getEmail().equals(user.getEmail())) {
                if(user.getId() != userSet.getId()) {
                    log.warn("Email {} уже существует в базе. Элемент в таким же Email {}",user.getName(),userSet);
                    throw new CustomValidationException("Email is using.");
                }
            }

        }
        users.remove(user);
        users.add(user);
        return user;
    }

}
