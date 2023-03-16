package ru.yandex.practicum.filmorate.exeption;

public class FilmNameAlreadyExistException extends RuntimeException{
    public FilmNameAlreadyExistException(String message) {
        super(message);
    }
}
