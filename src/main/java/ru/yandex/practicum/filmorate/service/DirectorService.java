package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dao.director.DirectorDao;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorDao directorDao;

    public Director addDirector(Director director) {
        return directorDao.addDirector(director);
    }

    public Director changeDirector(Director director) {
        return directorDao.changeDirector(director);
    }

    public Director getDirector(int id) {
        return directorDao.getDirector(id);
    }

    public List<Director> getDirectors() {
        return directorDao.getDirectors();
    }

    public void deleteDirector(int id) {
        directorDao.deleteDirector(id);
    }
}
