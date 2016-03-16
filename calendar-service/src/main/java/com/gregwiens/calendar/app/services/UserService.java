package com.gregwiens.calendar.app.services;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gregwiens.calendar.app.dao.UserRepository;
import com.gregwiens.calendar.app.model.User;

import static com.gregwiens.calendar.app.services.ValidationUtils.*;

import java.util.regex.Pattern;

/**
 *
 * Business service for User entity related operations
 *
 */
@Service
public class UserService {

    private static final Logger LOGGER = Logger.getLogger(UserService.class);
    private static final Long DEFAULT_MAX_CAL_PER_DAY = 2000L;

    private static final Pattern PASSWORD_REGEX = Pattern.compile("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,}");

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    @Autowired
    private UserRepository userRepository;

    /**
     *
     * updates the maximum Minutes of a given user
     *
     * @param username - the currently logged in user
     * @param newMaxMinutes - the new max daily Minutes for the user
     */
    @Transactional
    public void updateUserMaxMinutesPerDay(String username, Long newMaxMinutes) {
        User user = userRepository.findUserByUsername(username);

        if (user != null) {
            user.setMaxMinutesPerDay(newMaxMinutes);
        } else {
            LOGGER.info("User with username " + username + " could not have the max Minutes updated.");
        }
    }

    /**
     *
     * creates a new user in the database
     *
     * @param username - the username of the new user
     * @param email - the user email
     * @param role - the user role
     * @param password - the user plain text password
     */
    @Transactional
    public void createUser(String username, String email, String role, String password) {

        assertNotBlank(username, "Username cannot be empty.");
        assertMinimumLength(username, 6, "Username must have at least 6 characters.");
        assertNotBlank(email, "Email cannot be empty.");
        assertMatches(email, EMAIL_REGEX, "Invalid email.");
        assertNotBlank(role, "Role cannot be empty.");
        assertNotBlank(password, "Password cannot be empty.");
        assertMatches(password, PASSWORD_REGEX, "Password must have at least 6 characters, with 1 numeric and 1 uppercase character.");

        if (!userRepository.isUsernameAvailable(username)) {
            throw new IllegalArgumentException("The username is not available.");
        }

        User user = new User(username, new BCryptPasswordEncoder().encode(password), email, role, DEFAULT_MAX_CAL_PER_DAY);

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Transactional(readOnly = true)
    public Long findTodaysMinutesForUser(String username) {
        return userRepository.findTodaysMinutesForUser(username);
    }

}
