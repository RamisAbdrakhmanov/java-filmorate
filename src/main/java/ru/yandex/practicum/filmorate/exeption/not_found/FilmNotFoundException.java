package ru.yandex.practicum.filmorate.exeption.not_found;

public class FilmNotFoundException extends RuntimeException {
    public FilmNotFoundException(String message) {
        super(message);
    }
}
