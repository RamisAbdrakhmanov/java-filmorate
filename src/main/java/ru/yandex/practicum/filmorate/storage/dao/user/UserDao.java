package ru.yandex.practicum.filmorate.storage.dao.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface UserDao {

    List<User> getUsers();

    User getUserById(Integer id);

    User addUser(User user);

    User updateUser(User user);

    void deleteUserById(Integer id);

    Event addEvent(Event event);

    List<Event> getFeed(Integer userId);
}
