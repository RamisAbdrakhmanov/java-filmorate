package ru.yandex.practicum.filmorate.exeption.not_found;

public class GenreNotFoundException extends RuntimeException {
    public GenreNotFoundException(String message) {
        super(message);
    }
}