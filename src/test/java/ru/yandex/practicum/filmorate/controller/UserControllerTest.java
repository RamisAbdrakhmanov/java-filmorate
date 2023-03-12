package ru.yandex.practicum.filmorate.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserController controller;

    @Test
    public void contextLoads() {
        assertThat(controller).isNotNull();
    }

    @Test
    public void showUsersTest() throws Exception {
        LocalDate date = LocalDate.of(1980, 8, 12);
        User user = User.builder().name("Anna").email("anna@mail.ru").login("Anka").birthday(date).build();
        controller.addUser(user);

        String userJson = "[{\"id\":1,\"email\":\"anna@mail.ru\"," +
                "\"login\":\"Anka\",\"name\":\"Anna\"," +
                "\"birthday\":\"1980-08-12\"}]";
        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(userJson));

    }

    @Test
    public void addUsersTest() throws Exception {
        String userJson = "{\"id\":18," +
                "\"email\":\"mail@mail.ru\"," +
                "\"login\":\"dolore\"," +
                "\"name\":\"Nick Name\"," +
                "\"birthday\":\"1946-08-20\"}";
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk()) .andExpect(content().string(userJson));
    }

    @Test
    public void addUsersTestFailEmail() throws Exception {
        String userJson = "{\"id\":18," +
                "\"email\":\"mailmail.ru\"," +
                "\"login\":\"dolore\"," +
                "\"name\":\"Nick Name\"," +
                "\"birthday\":\"1946-08-20\"}";
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void addUsersTestFailLogin() throws Exception {
        String userJson = "{\"id\":18," +
                "\"email\":\"mail@mail.ru\"," +
                "\"login\":\"\"," +
                "\"name\":\"Nick Name\"," +
                "\"birthday\":\"1946-08-20\"}";
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void addUsersTestFailBirthday() throws Exception {
        LocalDate date = LocalDate.of(2044, 8, 12);

        String userJson = "{\"id\":18," +
                "\"email\":\"mail@mail.ru\"," +
                "\"login\":\"dolore\"," +
                "\"name\":\"Nick Name\"," +
                "\"birthday\":\""+date+"\"}";
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void addUsersTestWithEmptyName() throws Exception {
        String userJson = "{\"id\":254," +
                "\"email\":\"doloresAmbr@mail.ru\"," +
                "\"login\":\"dolore\"," +
                "\"name\":\"\"," +
                "\"birthday\":\"1946-08-20\"}";
        mockMvc.perform(post("/users").content(userJson))
                .andExpect(status().isOk()).andExpect(content().string(containsString("\"name\":\"dolore\"")));

    }

    @Test
    public void changeUsersTest() throws Exception {
        LocalDate date = LocalDate.of(1980, 8, 12);
        User user = User.builder().id(716).name("Anna").email("annaPUT@mail.ru").login("Anka").birthday(date).build();
        controller.addUser(user);

        String userJson = "{\"id\":716," +
                "\"email\":\"annaPUT@mail.ru\"," +
                "\"login\":\"Anasdka\"," +
                "\"name\":\"Anna\"," +
                "\"birthday\":\""+date+"\"}";
        mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk()).andExpect(content().string(userJson));



    }


}
