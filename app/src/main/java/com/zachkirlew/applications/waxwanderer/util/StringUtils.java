package com.zachkirlew.applications.waxwanderer.util;

public class StringUtils {

    public static String getFirstName(String fullName) {
        return fullName.split(" ")[0];
    }
}
