package com.gregwiens.calendar.app.dto;

/**
 *
 * JSON-serializable DTO containing user data
 *
 */
public class UserInfoDTO {

    private String userName;
    private String role;
    private Long maxMinutesPerDay;
    private Long todaysMinutes;

    public UserInfoDTO(String userName, String role, Long maxMinutesPerDay, Long todaysMinutes) {
        this.userName = userName;
        this.role = role;
        this.maxMinutesPerDay = maxMinutesPerDay;
        this.todaysMinutes = todaysMinutes;
    }

    public Long getMaxMinutesPerDay() {
        return maxMinutesPerDay;
    }

    public void setMaxMinutesPerDay(Long maxMinutesPerDay) {
        this.maxMinutesPerDay = maxMinutesPerDay;
    }

    public Long getTodaysMinutes() {
        return todaysMinutes;
    }

    public void setTodaysMinutes(Long todaysMinutes) {
        this.todaysMinutes = todaysMinutes;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
