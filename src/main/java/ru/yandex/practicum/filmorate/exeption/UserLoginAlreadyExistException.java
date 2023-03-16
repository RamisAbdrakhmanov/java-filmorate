package ru.yandex.practicum.filmorate.exeption;

public class UserLoginAlreadyExistException extends RuntimeException {
    public UserLoginAlreadyExistException(String message) {
        super(message);
    }
}
