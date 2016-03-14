package com.gregwiens.calendar.app.services;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gregwiens.calendar.app.dao.EventRepository;
import com.gregwiens.calendar.app.dao.UserRepository;
import com.gregwiens.calendar.app.dto.EventDTO;
import com.gregwiens.calendar.app.model.Event;
import com.gregwiens.calendar.app.model.SearchResult;
import com.gregwiens.calendar.app.model.User;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.gregwiens.calendar.app.services.ValidationUtils.assertNotBlank;
import static org.springframework.util.Assert.notNull;

/**
 *
 * Business service for Event-related operations.
 *
 */
@Service
public class EventService {

    private static final Logger LOGGER = Logger.getLogger(EventService.class);

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;

    /**
     *
     * searches events by date/time
     *
     * @param username - the currently logged in user
     * @param fromDate - search from this date, including
     * @param toDate - search until this date, including
     * @param fromTime - search from this time, including
     * @param toTime - search to this time, including
     * @param pageNumber - the page number (each page has 10 entries)
     * @return - the found results
     */
    @Transactional(readOnly = true)
    public SearchResult<Event> findEvents(String username, Date fromDate, Date toDate, Time fromTime, Time toTime, int pageNumber) {

        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("Both the from and to date are needed.");
        }

        if (fromDate.after(toDate)) {
            throw new IllegalArgumentException("From date cannot be after to date.");
        }

        if (fromDate.equals(toDate) && fromTime != null && toTime != null && fromTime.after(toTime)) {
            throw new IllegalArgumentException("On searches on the same day, from time cannot be after to time.");
        }

        Long resultsCount = eventRepository.countEventsByDateTime(username, fromDate, toDate, fromTime, toTime);

        List<Event> events = eventRepository.findEventsByDateTime(username, fromDate, toDate, fromTime, toTime, pageNumber);

        return new SearchResult<>(resultsCount, events);
    }

    /**
     *
     * deletes a list of events, given their Ids
     *
     * @param deletedEventIds - the list of events to delete
     */
    @Transactional
    public void deleteEvents(List<Long> deletedEventIds) {
        notNull(deletedEventIds, "deletedEventsId is mandatory");
        deletedEventIds.stream().forEach((deletedEventId) -> eventRepository.delete(deletedEventId));
    }

    /**
     *
     * saves a event (new or not) into the database.
     *
     * @param username - - the currently logged in user
     * @param id - the database ud of the event
     * @param date - the date the event took place
     * @param time - the time the event took place
     * @param description - the description of the event
     * @param minutes - the minutes of the event
     * @return - the new version of the event
     */

    @Transactional
    public Event saveEvent(String username, Long id, Date date, Time time, String description, Long minutes) {

        assertNotBlank(username, "username cannot be blank");
        notNull(date, "date is mandatory");
        notNull(time, "time is mandatory");
        notNull(description, "description is mandatory");
        notNull(minutes, "minutes is mandatory");

        Event event = null;

        if (id != null) {
            event = eventRepository.findEventById(id);

            event.setDate(date);
            event.setTime(time);
            event.setDescription(description);
            event.setMinutes(minutes);
        } else {
            User user = userRepository.findUserByUsername(username);

            if (user != null) {
                event = eventRepository.save(new Event(user, date, time, description, minutes));
                LOGGER.warn("A event was attempted to be saved for a non-existing user: " + username);
            }
        }

        return event;
    }

    /**
     *
     * saves a list of events (new or not) into the database
     *
     * @param username - the currently logged in user
     * @param events - the list of events to be saved
     * @return - the new versions of the saved events
     */
    @Transactional
    public List<Event> saveEvents(String username, List<EventDTO> events) {
        return events.stream()
                .map((event) -> saveEvent(
                        username,
                        event.getId(),
                        event.getDate(),
                        event.getTime(),
                        event.getDescription(),
                        event.getMinutes()))
                .collect(Collectors.toList());
    }
}
