package com.gregwiens.calendar.app.controllers;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.gregwiens.calendar.app.dto.EventDTO;
import com.gregwiens.calendar.app.dto.EventsDTO;
import com.gregwiens.calendar.app.model.Event;
import com.gregwiens.calendar.app.model.SearchResult;
import com.gregwiens.calendar.app.services.EventService;

import java.security.Principal;
import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 *  REST service for events - allows to update, create and search for events for the currently logged in user.
 *
 */
@Controller
@RequestMapping("event")
public class EventController {

    Logger LOGGER = Logger.getLogger(EventController.class);

    private static final long DAY_IN_MS = 1000 * 60 * 60 * 24;


    @Autowired
    private EventService eventService;

    /**
     * search Events for the current user by date and time ranges.
     *
     *
     * @param principal  - the current logged in user
     * @param fromDate - search from this date, including
     * @param toDate - search until this date, including
     * @param fromTime - search from this time, including
     * @param toTime - search to this time, including
     * @param pageNumber - the page number (each page has 10 entries)
     * @return - @see EventsDTO with the current page, total pages and the list of events
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public EventsDTO searchEventsByDate(
            Principal principal,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") Date fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") Date toDate,
            @RequestParam(value = "fromTime", required = false) @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm") Date fromTime,
            @RequestParam(value = "toTime", required = false) @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm") Date toTime,
            @RequestParam(value = "pageNumber") Integer pageNumber) {

        if (fromDate == null && toDate == null) {
            fromDate = new Date(System.currentTimeMillis() - (3 * DAY_IN_MS));
            toDate = new Date();
        }

        SearchResult<Event> result = eventService.findEvents(
                principal.getName(),
                fromDate,
                toDate,
                fromTime != null ? new Time(fromTime.getTime()) : null,
                toTime != null ? new Time(toTime.getTime()) : null,
                pageNumber);

        Long resultsCount = result.getResultsCount();
        Long totalPages = resultsCount / 10;

        if (resultsCount % 10 > 0) {
            totalPages++;
        }

        return new EventsDTO(pageNumber, totalPages, EventDTO.mapFromEventsEntities(result.getResult()));
    }

    /**
     *
     * saves a list of events - they be either new or existing
     *
     * @param principal - the current logged in user
     * @param events - the list of events to save
     * @return - an updated version of the saved events
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.POST)
    public List<EventDTO> saveEvents(Principal principal, @RequestBody List<EventDTO> events) {

        List<Event> savedEvents = eventService.saveEvents(principal.getName(), events);

        return savedEvents.stream()
                .map(EventDTO::mapFromEventEntity)
                .collect(Collectors.toList());
    }

    /**
     *
     * deletes a list of events
     *
     * @param deletedEventIds - the ids of the events to be deleted
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteEvents(@RequestBody List<Long> deletedEventIds) {
        eventService.deleteEvents(deletedEventIds);
    }

    /**
     *
     * error handler for backend errors - a 400 status code will be sent back, and the body
     * of the message contains the exception text.
     *
     * @param exc - the exception caught
     */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> errorHandler(Exception exc) {
        LOGGER.error(exc.getMessage(), exc);
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
    }


}
