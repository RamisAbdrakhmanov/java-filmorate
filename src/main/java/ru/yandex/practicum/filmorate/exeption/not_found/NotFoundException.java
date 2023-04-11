package ru.yandex.practicum.filmorate.exeption.not_found;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
