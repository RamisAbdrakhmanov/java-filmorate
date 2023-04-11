package ru.yandex.practicum.filmorate.exeption.not_found;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
