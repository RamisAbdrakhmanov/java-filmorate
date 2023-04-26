package ru.yandex.practicum.filmorate.storage.dao.like;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface LikeDao {

    void addLike(int filmID, int userID);

    void deleteLike(int filmID, int userID);

    List<Integer> showLikesSort(int count);
}
