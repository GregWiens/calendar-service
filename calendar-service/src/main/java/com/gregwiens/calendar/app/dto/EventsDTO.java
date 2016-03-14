package com.gregwiens.calendar.app.dto;

import java.util.List;

/**
 *
 * JSON serializable DTO containing data concerning a event search request.
 *
 */
public class EventsDTO {

    private long currentPage;
    private long totalPages;
    List<EventDTO> events;

    public EventsDTO(long currentPage, long totalPages, List<EventDTO> events) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.events = events;
    }

    public long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<EventDTO> getEvents() {
        return events;
    }

    public void setEvents(List<EventDTO> events) {
        this.events = events;
    }
}
