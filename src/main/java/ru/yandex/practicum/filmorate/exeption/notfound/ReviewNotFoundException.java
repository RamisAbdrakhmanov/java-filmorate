package ru.yandex.practicum.filmorate.exeption.notfound;

public class ReviewNotFoundException extends NotFoundException {
    public ReviewNotFoundException(String message) {
        super(message);
    }
}
