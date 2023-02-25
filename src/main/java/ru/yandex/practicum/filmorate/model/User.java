package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
public class User {


    private int id;
    @Email(message = "It's not email.")
    private String email;
    @NotBlank(message = "User login cannot be empty and contains spaces.")
    private String login;
    private String name;
    @Past(message = "date of birth cannot be in the future.")
    private LocalDate birthday;
}

