package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @PostMapping
    public Director addDirector(@Valid @RequestBody Director director) {
        log.info("Запрос на добавление режиссёра - {}", director);
        return directorService.addDirector(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        log.info("Запрос на изменение режиссёра - {}", director);
        return directorService.updateDirector(director);
    }

    @GetMapping
    public List<Director> getDirectors() {
        log.info("Запрос на получение списка всех режиссеров");
        return directorService.getDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable(name = "id") Integer id) {
        log.info("Запрос на получение режиссера с id = {}", id);
        return directorService.getDirector(id);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable(name = "id") Integer id) {
        log.info("Запрос на удаление режиссера с id = {}", id);
        directorService.deleteDirector(id);
    }
}
