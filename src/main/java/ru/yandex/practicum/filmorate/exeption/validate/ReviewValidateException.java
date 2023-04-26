package ru.yandex.practicum.filmorate.exeption.validate;

public class ReviewValidateException extends RuntimeException {
    public ReviewValidateException(String message) {
        super(message);
    }
}
