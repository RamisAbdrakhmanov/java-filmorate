package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
@Slf4j
public class DirectorController {
    private final DirectorService directorService;

    @PostMapping
    public Director addDirector(@Valid Director director) {
        log.info("Запрос на добавление режиссёра");
        return directorService.addDirector(director);
    }

    @PutMapping
    public Director changeDirector(@Valid Director director) {
        log.info("Запрос на изменение режиссёра с id - {}", director.getId());
        return directorService.changeDirector(director);
    }

    @GetMapping
    public List<Director> getDirectors() {
        return directorService.getDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable(name = "id") int id)  {
        return directorService.getDirector(id);
    }
}
