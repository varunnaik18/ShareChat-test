package co.sharechattest.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Varun on 03/05/17.
 */

public class Utility {

    public static String convertDate(long postedOn) {

        String dateText = "";

        if (Check.isEmpty(postedOn))
            return "";

        try {
            Date date = new Date(postedOn);
            SimpleDateFormat df2 = new SimpleDateFormat("dd/mm/yy");
            dateText = df2.format(date);
            return dateText;

        } catch (Exception e) {
            return dateText;
        }
    }

    public static int getAge(String dob) {

        int totalAge = 0;

        if (Check.isEmpty(dob))
            return totalAge;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy");
            Date d = sdf.parse(dob);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            totalAge = (Calendar.getInstance().get(Calendar.YEAR)) - (cal.get(Calendar.YEAR));

            return totalAge;
        } catch (ParseException e) {
            e.printStackTrace();
            return totalAge;
        }

    }

}
