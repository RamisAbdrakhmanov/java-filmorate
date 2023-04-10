package ru.yandex.practicum.filmorate.exeption.not_found;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
