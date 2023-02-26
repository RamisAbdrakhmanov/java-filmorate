package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
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
    public Set<User> getUsers(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return users;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getId() == 0) {
            user.setId(idCount);
            idCount++;
        }

        users.add(user);
        return user;
    }

    @PutMapping
    public User changeUser(@Valid @RequestBody User user, HttpServletRequest request) {

        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        for (User userSet : users) {
            if (userSet.getEmail().equals(user.getEmail())) {
                throw new ValidationException("Email is using");
            }

        }
        users.remove(user);
        users.add(user);
        return user;
    }

}
