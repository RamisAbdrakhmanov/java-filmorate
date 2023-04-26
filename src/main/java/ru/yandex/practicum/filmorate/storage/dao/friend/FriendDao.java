package ru.yandex.practicum.filmorate.storage.dao.friend;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface FriendDao {
    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<Integer> showFriendsById(int id);

}
