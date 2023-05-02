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

    public Director updateDirector(Director director) {
        return directorDao.updateDirector(director);
    }

    public Director getDirector(Integer id) {
        return directorDao.getDirector(id);
    }

    public List<Director> getDirectors() {
        return directorDao.getDirectors();
    }

    public void deleteDirector(Integer id) {
        directorDao.deleteDirector(id);
    }
}
