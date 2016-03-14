package com.gregwiens.calendar.app;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gregwiens.calendar.app.dto.EventDTO;
import com.gregwiens.calendar.app.model.Event;
import com.gregwiens.calendar.app.model.SearchResult;
import com.gregwiens.calendar.app.services.EventService;
import com.gregwiens.calendar.config.root.RootContextConfig;
import com.gregwiens.calendar.config.root.TestConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;

import static com.gregwiens.calendar.app.TestUtils.date;
import static com.gregwiens.calendar.app.TestUtils.time;
import static com.gregwiens.calendar.app.dto.EventDTO.mapFromEventEntity;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes={TestConfiguration.class, RootContextConfig.class})
public class EventServiceTest {

    @Autowired
    private EventService eventService;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void testFindEventsByDate() {
        SearchResult<Event> result = eventService.findEvents(UserServiceTest.USERNAME, date(2015,1,1), date(2015,1,2), null ,null, 1);
        assertTrue("results not expected, total " + result.getResultsCount(), result.getResultsCount() == 4);
    }

    @Test
    public void testFindEventsByDateTime() {
        SearchResult<Event> result = eventService.findEvents(UserServiceTest.USERNAME, date(2015,1,1), date(2015,1,2),
                time("11:00") ,time("14:00"), 1);
        assertTrue("results not expected, total " + result.getResultsCount(), result.getResultsCount() == 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromDateAfterToDate() {
        eventService.findEvents(UserServiceTest.USERNAME, date(2015,1,2), date(2015,1,1), null ,null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromTimeAfterToTime() {
        eventService.findEvents(UserServiceTest.USERNAME, date(2015,1,2), date(2015,1,1), time("12:00") ,time("11:00"), 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromDateNull() {
        eventService.findEvents(UserServiceTest.USERNAME, null, date(2015,1,1), time("12:00") ,time("11:00"), 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void toDateNull() {
        eventService.findEvents(UserServiceTest.USERNAME, date(2015,1,1), null, time("12:00") ,time("11:00"), 1);
    }

    @Test
    public void deleteEvents() {
        eventService.deleteEvents(Arrays.asList(15L));
        Event event = em.find(Event.class, 15L);
        assertNull("event was not deleted" , event);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteEventsNull() {
        eventService.deleteEvents(null);
    }

    @Test
    public void saveEvents() {
        EventDTO event1 = mapFromEventEntity(em.find(Event.class, 1L));
        EventDTO event2 = mapFromEventEntity(em.find(Event.class, 2L));

        event1.setDescription("test1");
        event2.setMinutes(10L);

        List<EventDTO> events = Arrays.asList(event1, event2);

        eventService.saveEvents(UserServiceTest.USERNAME, events);


        Event m1 = em.find(Event.class, 1L);
        assertTrue("description not as expected: " + m1.getDescription(), "test1".equals(m1.getDescription()));

        Event m2 = em.find(Event.class, 2L);
        assertTrue("minutes not as expected: " + m2.getMinutes(), m2.getMinutes() == 10L);
    }


}
