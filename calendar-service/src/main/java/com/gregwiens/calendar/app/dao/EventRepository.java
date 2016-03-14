package com.gregwiens.calendar.app.dao;


import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.gregwiens.calendar.app.model.Event;
import com.gregwiens.calendar.app.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * Repository class for the Event entity
 *
 */
@Repository
public class EventRepository {

    private static final Logger LOGGER = Logger.getLogger(EventRepository.class);

    @PersistenceContext
    EntityManager em;

    /**
     *
     * counts the matching events, given the bellow criteria
     *
     * @param username - the currently logged in username
     * @param fromDate - search from this date, including
     * @param toDate - search until this date, including
     * @param fromTime - search from this time, including
     * @param toTime - search to this time, including
     * @return -  a list of matching events, or an empty collection if no match found
     */
    public Long countEventsByDateTime(String username, Date fromDate, Date toDate, Time fromTime, Time toTime) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // query for counting the total results
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Event> countRoot = cq.from(Event.class);
        cq.select((cb.count(countRoot)));
        cq.where(getCommonWhereCondition(cb, username, countRoot, fromDate, toDate, fromTime, toTime));
        Long resultsCount = em.createQuery(cq).getSingleResult();

        LOGGER.info("Found " + resultsCount + " results.");

        return resultsCount;
    }

    /**
     *
     * finds a list of events, given the bellow criteria
     *
     * @param username - the currently logged in username
     * @param fromDate - search from this date, including
     * @param toDate - search until this date, including
     * @param fromTime - search from this time, including
     * @param toTime - search to this time, including
     * @return -  a list of matching events, or an empty collection if no match found
     */
    public List<Event> findEventsByDateTime(String username, Date fromDate, Date toDate,
                                          Time fromTime, Time toTime, int pageNumber) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // the actual search query that returns one page of results
        CriteriaQuery<Event> searchQuery = cb.createQuery(Event.class);
        Root<Event> searchRoot = searchQuery.from(Event.class);
        searchQuery.select(searchRoot);
        searchQuery.where(getCommonWhereCondition(cb, username, searchRoot, fromDate, toDate, fromTime, toTime));

        List<Order> orderList = new ArrayList();
        orderList.add(cb.desc(searchRoot.get("date")));
        orderList.add(cb.asc(searchRoot.get("time")));
        searchQuery.orderBy(orderList);

        TypedQuery<Event> filterQuery = em.createQuery(searchQuery)
                .setFirstResult((pageNumber - 1) * 10)
                .setMaxResults(10);

        return filterQuery.getResultList();
    }

    /**
     * Delete a event, given its identifier
     *
     * @param deletedEventId - the id of the event to be deleted
     */
    public void delete(Long deletedEventId) {
        Event delete = em.find(Event.class, deletedEventId);
        em.remove(delete);
    }

    /**
     *
     * finds a event given its id
     *
     */
    public Event findEventById(Long id) {
        return em.find(Event.class, id);
    }

    /**
     *
     * save changes made to a event, or create the event if its a new event.
     *
     */
    public Event save(Event event) {
        return em.merge(event);
    }


    private Predicate[] getCommonWhereCondition(CriteriaBuilder cb, String username, Root<Event> searchRoot, Date fromDate, Date toDate,
                                                Time fromTime, Time toTime) {

        List<Predicate> predicates = new ArrayList<>();
        Join<Event, User> user = searchRoot.join("user");

        predicates.add(cb.equal(user.<String>get("username"), username));
        predicates.add(cb.greaterThanOrEqualTo(searchRoot.<Date>get("date"), fromDate));

        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(searchRoot.<Date>get("date"), toDate));
        }

        if (fromTime != null) {
            predicates.add(cb.greaterThanOrEqualTo(searchRoot.<Date>get("time"), fromTime));
        }

        if (toTime != null) {
            predicates.add(cb.lessThanOrEqualTo(searchRoot.<Date>get("time"), toTime));
        }

        return predicates.toArray(new Predicate[]{});
    }

}
