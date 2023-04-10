package ru.yandex.practicum.filmorate.storage.dao.friend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.not_found.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.storage.mapper.FriendMapper;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FriendDbDao implements FriendDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(int userId, int friendId) {
        log.info("запрос на добавления в друзья от {} к {}", userId, friendId);
        switch (checkFriend(userId, friendId)) {
            case 1:
                log.info("User с id - {}, уже отправлял заявку friend с id - {}", userId, friendId);
                break;
            case 0:
                log.info("User с id - {}, подал заявку user с id - {}", userId, friendId);
                jdbcTemplate.update("INSERT INTO friends (user_id, friend_id, status) " +
                        "VALUES(?, ?, ?)", userId, friendId, false);
                break;
            case -1:
                log.info("User с id - {}, принял заявку friend с id - {}", friendId, userId);
                jdbcTemplate.update(""
                                + "UPDATE friends "
                                + "SET user_id=?, friend_id=?, status=? "
                                + "WHERE user_id=? "
                                + "AND friend_id=? ",
                        friendId,
                        userId,
                        true,
                        friendId,
                        userId);
                break;

        }

    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        log.info("запрос на удаление из друзей от {} к {}", userId, friendId);
        switch (checkFriend(userId, friendId)) {
            case 0:
                log.info("Не были друзьями");
                return;
            case -1:
                log.info("Заменили местами user id.");
                int swap = userId;
                userId = friendId;
                friendId = swap;
        }

        Friend result = get(userId, friendId);

        if (result.getStatus()) {
            log.info("один из user отозвал подтверждение дружбы");
            jdbcTemplate.update(""
                    + "UPDATE friends "
                    + "SET status = false "
                    + "WHERE user_id=? "
                    + "AND friend_id=?", userId, friendId);

        } else {
            log.info("оба user отозвали подтверждение дружбы");
            jdbcTemplate.update(""
                    + "DELETE FROM friends "
                    + "WHERE user_id=? "
                    + "AND friend_id=?", userId, friendId);
        }

    }

    @Override
    public List<Integer> showFriendsById(int id) {
        log.info("запрос на вывод друзей пользователя ID - {}", id);

        List<Integer> friend = jdbcTemplate.query(format(""
                        + "SELECT user_id, friend_id, status "
                        + "FROM friends "
                        + "WHERE user_id=%d "
                        + "OR (friend_id=%d and status=%b)  ", id, id, true), new FriendMapper())
                .stream()
                .map(friend1 -> {
                    if (friend1.getFriendId() != id) {
                        return friend1.getFriendId();
                    } else {
                        return friend1.getUserId();
                    }
                }).distinct().collect(Collectors.toList());
        return friend;
    }

    private Friend get(int userId, int friendId) {
        log.info("запрос на одну отдельную заявку друзей");
        return jdbcTemplate.queryForObject(format(""
                + "SELECT user_id, friend_id, status "
                + "FROM friends "
                + "WHERE user_id=%d "
                + "AND friend_id=%d", userId, friendId), new FriendMapper());
    }

    private byte checkFriend(int userId, int friendId) {
        log.info("проверка записи друзей");
        try {
            log.debug("user_id: {}, уже добавил friend_id: {}", userId, friendId);
            Friend friend = jdbcTemplate.queryForObject(format(""
                    + "SELECT user_id, friend_id, status "
                    + "FROM friends "
                    + "WHERE user_id=%d "
                    + "AND friend_id=%d ", userId, friendId), new FriendMapper());
            log.info("запись имеется в правильном формате {}", friend);
            return 1;

        } catch (EmptyResultDataAccessException ignored) {
            log.info("пустая обработка EmptyResultDataAccessException");

        }
        try {
            log.info("user_id: {} пытается одобрить заявку friend_id: {}", userId, friendId);
            Friend friend = jdbcTemplate.queryForObject(format(""
                    + "SELECT user_id, friend_id, status "
                    + "FROM friends "
                    + "WHERE user_id=%d "
                    + "AND friend_id=%d ", friendId, userId), new FriendMapper());
            log.info("запись имеется в развернутом формате {}", friend);
            if (friend.getStatus()) {
                log.debug("user_id: {} уже одобрить заявку friend_id: {}", userId, friendId);
                throw new UserNotFoundException("Уже добавлены в друзья");
            }
            log.debug("user_id: {} может одобрить заявку friend_id: {}", userId, friendId);
            return -1;

        } catch (EmptyResultDataAccessException e) {
            log.info("запись друзей отсутствует");
            return 0;
        }
    }
}
