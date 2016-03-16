package com.gregwiens.calendar.app.init;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gregwiens.calendar.app.model.Event;
import com.gregwiens.calendar.app.model.User;

import javax.persistence.EntityManagerFactory;
import java.sql.Time;
import java.util.Date;

/**
 *
 * This is a initializing bean that inserts some test data in the database. It is only active in
 * the development profile, to see the data login with user123 / PAssword2 and do a search starting on
 * 1st of January 2015.
 *
 */
@Component
public class TestDataInitializer {

    @Autowired
    private EntityManagerFactory entityManagerFactory;


    public void init() throws Exception {

        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        User support = new User("support123", "$2a$10$x9vXeDsSC2109FZfIJz.pOZ4dJ056xBpbesuMJg3jZ.ThQkV119tS", "test@email.com", "support", 300L);

        session.persist(support);

        User admin = new User("admin123", "$2a$10$x9vXeDsSC2109FZfIJz.pOZ4dJ056xBpbesuMJg3jZ.ThQkV119tS", "test@email.com", "admin", 300L);

        session.persist(admin);

        User user = new User("test123", "$2a$10$x9vXeDsSC2109FZfIJz.pOZ4dJ056xBpbesuMJg3jZ.ThQkV119tS", "test@email.com", "owner", 300L);

        session.persist(user);

        session.persist(new Event(user, new Date(115, 0, 1), new Time(12, 0, 0), "Meeting with Dave", 30L));
        session.persist(new Event(user, new Date(115, 0, 1), new Time(12, 0, 0), "Meeting with DBA", 30L));
        session.persist(new Event(user, new Date(115, 0, 1), new Time(12, 0, 0), "Meeting with Technical Architecture", 60L));
        session.persist(new Event(user, new Date(115, 0, 1), new Time(12, 0, 0), "Meeting with business owner", 30L));
        session.persist(new Event(user, new Date(115, 0, 1), new Time(12, 0, 0), "Meeting with team", 30L));
        session.persist(new Event(user, new Date(115, 0, 1), new Time(12, 0, 0), "Meeting with Scrum Master", 15L));
        session.persist(new Event(user, new Date(115, 0, 1), new Time(12, 0, 0), "Meeting with Mary and Tom", 30L));
        session.persist(new Event(user, new Date(115, 0, 1), new Time(12, 0, 0), "Demo 1", 60L));
        session.persist(new Event(user, new Date(115, 0, 1), new Time(12, 0, 0), "Demo 2", 60L));
        session.persist(new Event(user, new Date(115, 0, 1), new Time(12, 0, 0), "Demo 3", 60L));

        transaction.commit();
    }
}
