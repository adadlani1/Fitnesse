package and.coursework.fitnesse.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppUtils {

    /*Gets Current date */
    public static String getDate(String pattern) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
        Date date = new Date();
        return dateFormat.format(date);
    }
}
