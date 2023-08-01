package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmDao;
import ru.yandex.practicum.filmorate.storage.dao.friend.FriendDao;
import ru.yandex.practicum.filmorate.storage.dao.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.like.LikeDao;
import ru.yandex.practicum.filmorate.storage.dao.user.UserDao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserDao userDao;
    private final FriendDao friendDao;
    private final LikeDao likeDao;
    private final FilmDao filmDao;
    private final GenreDao genreDao;

    @Autowired
    public UserService(UserDao userDao, FriendDao friendDao, LikeDao likeDao, FilmDao filmDao, GenreDao genreDao) {
        this.userDao = userDao;
        this.friendDao = friendDao;
        this.likeDao = likeDao;
        this.filmDao = filmDao;
        this.genreDao = genreDao;
    }

    public void addFriend(Integer userID, Integer friendID) {
        checkUser(userID);
        checkUser(friendID);
        friendDao.addFriend(userID, friendID);
        userDao.addEvent(makeEvent("ADD", userID, friendID));
    }

    public void deleteFriend(Integer userID, Integer friendID) {
        checkUser(userID);
        checkUser(friendID);
        friendDao.deleteFriend(userID, friendID);
        userDao.addEvent(makeEvent("REMOVE", userID, friendID));
    }

    public List<User> getCommonFriends(Integer userID, Integer friendID) {
        checkUser(userID);
        checkUser(friendID);
        List<User> user = getFriends(userID);
        List<User> friend = getFriends(friendID);
        user.retainAll(friend);
        return user;
    }

    public List<User> getFriends(Integer userID) {
        checkUser(userID);
        List<Integer> friendId = friendDao.getFriendsById(userID);
        List<User> users = new ArrayList<>();
        for (int id : friendId) {
            checkUser(id);
            users.add(userDao.getUserById(id));
        }
        return users;
    }

    public List<Film> getUsersRecommendations(Integer userId) {
        checkUser(userId);
        List<Integer> allFilmsList = likeDao.getRecommendedList(userId);
        List<Integer> usersFilms = filmDao.getUsersLikedFilms(userId);
        allFilmsList.removeAll(usersFilms);
        return allFilmsList.stream()
                .map(filmDao::getFilmById)
                .peek(film -> film.setGenres(genreDao.getGenres(film.getId())))
                .collect(Collectors.toList());
    }

    public List<Event> getFeed(Integer userId) {
        checkUser(userId);
        return userDao.getFeed(userId);
    }

    public List<User> getUsers() {
        return userDao.getUsers();
    }

    public User getUserById(Integer id) {
        return userDao.getUserById(id);
    }

    public User addUser(User user) {
        return userDao.addUser(user);
    }

    public User updateUser(User user) {
        return userDao.updateUser(user);
    }

    public void deleteUserById(Integer id) {
        userDao.deleteUserById(id);
    }

    private void checkUser(Integer userId) {
        getUserById(userId);
    }

    private Event makeEvent(String operation, Integer userId, Integer friendId) {
        return Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(userId)
                .eventType("FRIEND")
                .operation(operation)
                .eventId(0)
                .entityId(friendId)
                .build();
    }
}
