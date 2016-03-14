package com.gregwiens.calendar.app.model;


import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Time;
import java.util.Date;

/**
 *
 * The Event JPA entity
 *
 */
@Entity
@Table(name = "EVENTS")
public class Event extends AbstractEntity {

    @ManyToOne
    private User user;

    private Date date;
    private Time time;
    private String description;
    private Long minutes;

    public Event() {

    }

    public Event(User user, Date date, Time time, String description, Long minutes) {
        this.user = user;
        this.date = date;
        this.time = time;
        this.description = description;
        this.minutes = minutes;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
