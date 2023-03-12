package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    public UserService() {
    }

    public void addFriend(User user, User friend) {
        user.addFriend(friend);
        friend.addFriend(user);
    }

    public void deleteFriend(User user, User friend) {
        user.removeFriend(friend);
        friend.removeFriend(user);
    }

    public Set<User> showCommonFriends(User user, User otherUser) {
        Set<User> users = new HashSet<>(user.getFriends());
        users.retainAll(otherUser.getFriends());
        return users;
    }

    public Set<User> showFriends(User user) {
        return user.getFriends();
    }
}
