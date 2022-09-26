package com.mamazinha.baby.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    public static final String SYSTEM = "system";
    public static final String DEFAULT_LANGUAGE = "en";

    public static final String THAT_IS_NOT_YOUR_BABY_PROFILE = "That is not your baby profile!";

    public static final String REACH_MAX_BABY_PROFILE_ALLOWED_FOR_USER_ROLE_AUTHORITY =
        "Reach max baby profile allowed for user role authority";

    private Constants() {}
}
