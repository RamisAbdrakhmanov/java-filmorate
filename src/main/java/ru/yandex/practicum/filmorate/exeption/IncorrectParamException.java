package ru.yandex.practicum.filmorate.exeption;

public class IncorrectParamException extends RuntimeException{
    public IncorrectParamException(String message) {
        super(message);
    }
}
