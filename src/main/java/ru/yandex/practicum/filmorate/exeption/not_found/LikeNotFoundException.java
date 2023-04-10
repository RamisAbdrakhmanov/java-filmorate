package ru.yandex.practicum.filmorate.exeption.not_found;

public class LikeNotFoundException extends RuntimeException {
    public LikeNotFoundException(String message) {
        super(message);
    }
}
