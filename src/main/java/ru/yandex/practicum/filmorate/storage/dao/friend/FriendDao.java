package ru.yandex.practicum.filmorate.storage.dao.friend;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface FriendDao {
    void addFriend(Integer userId, Integer friendId);

    void deleteFriend(Integer userId, Integer friendId);

    List<Integer> getFriendsById(Integer id);

}
