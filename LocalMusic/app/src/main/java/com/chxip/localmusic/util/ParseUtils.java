package com.chxip.localmusic.util;

/**
 */
public class ParseUtils {

    public static long parseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
