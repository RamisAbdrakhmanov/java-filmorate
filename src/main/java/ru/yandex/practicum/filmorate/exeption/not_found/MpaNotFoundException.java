package ru.yandex.practicum.filmorate.exeption.not_found;

public class MpaNotFoundException extends RuntimeException {
    public MpaNotFoundException(String message) {
        super(message);
    }
}
