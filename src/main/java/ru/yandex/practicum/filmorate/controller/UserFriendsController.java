package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Set;

@RestController
@RequestMapping("/users/{id}/friends")
public class UserFriendsController {

    private final UserStorage userStorage;
    private final UserService userService;

    public UserFriendsController(InMemoryUserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping
    public Set<User> showFriends(@PathVariable int id) {
        return userService.showFriends(getUserById(id));
    }

    @PutMapping("/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(getUserById(id), getUserById(friendId));
    }

    @DeleteMapping("/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(getUserById(id), getUserById(friendId));
    }

    @GetMapping("/common/{otherId}")
    public Set<User> showCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.showCommonFriends(getUserById(id), getUserById(otherId));
    }

    public User getUserById(int id) {
        return userStorage.showUserById(id);
    }
}
