package ru.yandex.practicum.filmorate.storage.dao.like;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface LikeDao {

    void addLike(Integer filmID, Integer userID);

    void deleteLike(Integer filmID, Integer userID);

    List<Integer> getLikesSort(Integer count);

    List<Integer> getRecommendedList(Integer userID);
}
