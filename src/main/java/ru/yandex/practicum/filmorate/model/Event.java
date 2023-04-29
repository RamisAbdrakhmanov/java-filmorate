package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@Builder
public class Event {
    @NotNull
    private long timestamp;
    private int userId;
    @NotNull
    private String eventType;
    @NotNull
    private String operation;
    private int eventId;
    private int entityId;

}
