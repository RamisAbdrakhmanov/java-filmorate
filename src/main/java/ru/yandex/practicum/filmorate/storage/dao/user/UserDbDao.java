package ru.yandex.practicum.filmorate.storage.dao.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.notfound.UserNotFoundException;
import ru.yandex.practicum.filmorate.exeption.validate.FilmNameAlreadyExistException;
import ru.yandex.practicum.filmorate.exeption.validate.UserEmailAlreadyExistException;
import ru.yandex.practicum.filmorate.exeption.validate.UserIdNotNullException;
import ru.yandex.practicum.filmorate.exeption.validate.UserLoginAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.EventMapper;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;

@Slf4j
@Primary
@Repository
@RequiredArgsConstructor
public class UserDbDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;


    @Override
    public List<User> getUsers() {
        List<User> users = jdbcTemplate.query(""
                + "SELECT user_id, email, login, name, birthday "
                + "FROM users", new UserMapper());
        return users;
    }

    @Override
    public User getUserById(Integer id) {
        try {
            User user = jdbcTemplate.queryForObject(format(""
                    + "SELECT user_id, email, login, name, birthday "
                    + "FROM users "
                    + "WHERE user_id=%d", id), new UserMapper());

            return user;
        } catch (EmptyResultDataAccessException e) {
            String message = String.format("Не возможно найти пользователя с id = %d", id);
            log.warn(message);
            throw new UserNotFoundException(message);
        }
    }

    @Override
    public User addUser(User user) {
        checkAdd(user);

        jdbcTemplate.update(""
                        + "INSERT INTO users (email, login, name, birthday) "
                        + "VALUES (?, ?, ?, ?)",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()));
        User result = jdbcTemplate.queryForObject(format(""
                + "SELECT user_id, email, login, name, birthday "
                + "FROM users "
                + "WHERE email='%s'", user.getEmail()), new UserMapper());

        return result;
    }

    @Override
    public User updateUser(User user) {
        checkChange(user);
        jdbcTemplate.update(""
                        + "UPDATE users "
                        + "SET email=?, login=?, name=?, birthday=? "
                        + "WHERE user_id=?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        User result = getUserById(user.getId());
        return result;
    }


    @Override
    public void deleteUserById(Integer id) {
        jdbcTemplate.update("DELETE " +
                "FROM users " +
                "WHERE user_id = ?", id);
    }

    @Override
    public Event addEvent(Event event) {
        String sql = "INSERT INTO events (timestamp, user_id, event_type, operation, entity_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"event_id"});
            ps.setLong(1, event.getTimestamp());
            ps.setInt(2, event.getUserId());
            ps.setString(3, event.getEventType());
            ps.setString(4, event.getOperation());
            ps.setInt(5, event.getEntityId());
            return ps;
        }, keyHolder);
        event.setEventId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return event;
    }

    @Override
    public List<Event> getFeed(Integer userId) {
        return jdbcTemplate.query("SELECT * FROM events WHERE user_id = ?", new EventMapper(), userId);
    }

    private void checkAdd(User user) {
        log.info("проверка на добавление user - {}", user);

        if (user.getId() != null) {
            log.error("user_id не должно иметь значение");
            throw new UserIdNotNullException("user_id не должно иметь значение");
        }


        try {
            User loginUser = jdbcTemplate.queryForObject(format(""
                    + "SELECT user_id, email, login, name, birthday "
                    + "FROM users "
                    + "WHERE login= '%s'", user.getLogin()), new UserMapper());

            log.error("Невозможно добить User. User c login - {} уже имеет ID - {}",
                    loginUser.getLogin(),
                    loginUser.getId());
            throw new UserLoginAlreadyExistException(
                    String.format("Невозможно добавить User. User c login - %s уже имеет ID - %s",
                            loginUser.getLogin(),
                            loginUser.getId()));

        } catch (EmptyResultDataAccessException e) { // эта ошибка обрабатывает NULL на выходе из базы,
            // иначе не нашел как мне получить или null или значение.
            log.info("User с login {} отсутствует!", user.getLogin());
        }

        try {
            User emailUser = jdbcTemplate.queryForObject(format(""
                    + "SELECT user_id, email, login, name, birthday "
                    + "FROM users "
                    + "WHERE name= '%s'", user.getEmail()), new UserMapper());

            log.error("Невозможно добавить user. User c email - {} уже имеет ID - {}",
                    emailUser.getEmail(),
                    emailUser.getId());

            throw new UserEmailAlreadyExistException(
                    String.format("Невозможно добавить user. User c email - %s уже имеет ID - %s",
                            emailUser.getEmail(),
                            emailUser.getId()));

        } catch (EmptyResultDataAccessException e) {
            log.info("User с email {} отсутствует!", user.getEmail());
        }

        if (user.getName().isBlank()) {
            log.debug("User не имеет имени");
            user.setName(user.getLogin());
        }
    }

    private void checkChange(User user) {
        log.info("проверка на изменение user - {}", user);
        getUserById(user.getId());
        try {
            User loginUser = jdbcTemplate.queryForObject(format(""
                    + "SELECT user_id, email, login, name, birthday "
                    + "FROM users "
                    + "WHERE login= '%s'", user.getLogin()), new UserMapper());

            if (!Objects.equals(loginUser.getId(), user.getId())) {
                log.error("Невозможно изменить login. User c login - {} уже имеет ID - {}",
                        loginUser.getLogin(),
                        loginUser.getId());
                throw new FilmNameAlreadyExistException(
                        String.format("Невозможно изменить login. User c login - %s уже имеет ID - %s",
                                loginUser.getLogin(),
                                loginUser.getId()));
            }

        } catch (EmptyResultDataAccessException e) { // эта ошибка обрабатывает NULL на выходе из базы,
            // иначе не нашел как мне получить или null или значение.
            log.info("User с логином {} отсутствует!", user.getLogin());
        }

        try {
            User emailUser = jdbcTemplate.queryForObject(format(""
                    + "SELECT user_id, email, login, name, birthday "
                    + "FROM users "
                    + "WHERE name= '%s'", user.getEmail()), new UserMapper());

            if (!Objects.equals(emailUser.getId(), user.getId())) {
                log.error("Невозможно изменить email. User c email - {} уже имеет ID - {}",
                        emailUser.getEmail(),
                        emailUser.getId());
                throw new FilmNameAlreadyExistException(
                        String.format("Невозможно изменить email. User c email - %s уже имеет ID - %s",
                                emailUser.getName(),
                                emailUser.getId()));
            }

        } catch (EmptyResultDataAccessException e) {
            log.info("User с email {} отсутствует!", user.getEmail());
        }

        if (user.getName().isBlank()) {
            log.debug("User не имеет имени");
            user.setName(user.getLogin());
        }


    }

}
