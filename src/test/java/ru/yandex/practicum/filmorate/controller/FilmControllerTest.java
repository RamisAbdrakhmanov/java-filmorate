package ru.yandex.practicum.filmorate.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FilmController controller;

    @Test
    public void contextLoads() {
        assertThat(controller).isNotNull();
    }

    @Test
    public void getFilmsTest() throws Exception {
        LocalDate date = LocalDate.of(2022, 12, 13);
        Film film = Film.builder().name("asd").description("asd").releaseDate(date).duration(12).build();

        controller.addFilm(film);

        String filmsJson = "[{\"id\":1,\"name\":\"asd\",\"description\":\"asd\"," +
                "\"releaseDate\":\"2022-12-13\",\"duration\":12}," +
                "{\"id\":14,\"name\":\"asdt\",\"description\":\"asd\"," +
                "\"releaseDate\":\"2022-12-13\",\"duration\":12}]";
        mockMvc.perform(get("/films"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(filmsJson));
    }

    @Test
    public void addFilmTest() throws Exception {
        String filmJson = "{\"id\":14,\"name\":\"asdt\",\"description\":\"asd\",\"releaseDate\":\"2022-12-13\",\"duration\":12}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isOk()).andExpect(content().string(filmJson));
    }

    @Test
    public void addFilmFailNameTest() throws Exception {
        String filmJson = "{\"name\":\"\",\"description\":\"asd\",\"releaseDate\":\"2022-12-13\",\"duration\":12}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void addFilmFailLDescriptionMin1Test() throws Exception {
        String filmJson = "{\"id\":1," +
                "\"name\":\"asd\"," +
                "\"\":\"asd\"," +
                "\"releaseDate\":\"2022-12-13\"," +
                "\"duration\":12}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void addFilmFailLDescriptionMax200Test() throws Exception {

        String description = "a".repeat(201);

        String filmJson = "{\"id\":1," +
                "\"name\":\"asd\"," +
                "\"" + description + "\":\"asd\"," +
                "\"releaseDate\":\"2022-12-13\"," +
                "\"duration\":12}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void addFilmFailReleaseDateTest() throws Exception {
        String filmJson = "{\"id\":1,\"name\":\"asdtr\",\"description\":\"asd\",\"releaseDate\":\"1888-12-13\",\"duration\":12}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void addFilmFailDurationTest() throws Exception {
        String filmJson = "{\"id\":1,\"name\":\"asd\",\"description\":\"asd\",\"releaseDate\":\"2022-12-13\",\"duration\":-1}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void putFilmTest() throws Exception {
        LocalDate date = LocalDate.of(2022, 12, 13);

        Film film = Film.builder().id(17)
                .description("asdgr")
                .name("asd123")
                .releaseDate(date)
                .duration(12)
                .build();

        controller.getFilms().add(film);

        String filmJson = "{\"id\":17,\"name\":\"asdgr\"," +
                "\"description\":\"assadd\"," +
                "\"releaseDate\":\"2022-12-13\"," +
                "\"duration\":12}";

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isOk())
                .andExpect(content().string(filmJson));
    }


}
