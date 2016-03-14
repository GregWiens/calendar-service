package com.gregwiens.calendar.app.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gregwiens.calendar.app.dto.serialization.CustomTimeDeserializer;
import com.gregwiens.calendar.app.dto.serialization.CustomTimeSerializer;
import com.gregwiens.calendar.app.model.Event;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * JSON serializable DTO containing Event data
 *
 */
public class EventDTO {

    private Long id;

    @JsonFormat(pattern = "yyyy/MM/dd", timezone = "CET")
    private Date date;

    @JsonSerialize(using = CustomTimeSerializer.class)
    @JsonDeserialize(using = CustomTimeDeserializer.class)
    private Time time;

    private String description;
    private Long minutes;

    public EventDTO() {
    }

    public EventDTO(Long id, Date date, Time time, String description, Long minutes) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.description = description;
        this.minutes = minutes;
    }

    public static EventDTO mapFromEventEntity(Event event) {
        return new EventDTO(event.getId(), event.getDate(), event.getTime(),
                event.getDescription(), event.getMinutes());
    }

    public static List<EventDTO> mapFromEventsEntities(List<Event> events) {
        return events.stream().map((event) -> mapFromEventEntity(event)).collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getMinutes() {
        return minutes;
    }

    public void setMinutes(Long minutes) {
        this.minutes = minutes;
    }

}
