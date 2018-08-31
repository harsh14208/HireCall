package app.com.thetechnocafe.hirecall.Utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.com.thetechnocafe.hirecall.Enums.TimeType;

/**
 * Created by gurleen on 21/4/17.
 */

public class DateFormatUtility {
    private static final DateFormatUtility ourInstance = new DateFormatUtility();

    public static DateFormatUtility getInstance() {
        return ourInstance;
    }

    private DateFormatUtility() {
    }

    public String convertLongToDate(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMMM yyyy");
        return simpleDateFormat.format(new Date(time));
    }

    public String convertLongToTime(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.format(new Date(time));
    }

    public String convertLongToDateSlashFormat(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d/MM/yy");
        return simpleDateFormat.format(new Date(time));
    }

    public String convertDateAndTimeToString(long dateTime) {
        long currentTime = new Date().getTime();

        if (currentTime - dateTime < 86400000) {
            return convertLongToTime(dateTime);
        } else {
            return convertLongToTime(dateTime) + " " + convertLongToDateSlashFormat(dateTime);
        }
    }

    public String convertDateAndTimeToStringForTodos(long time) {
        long currentTime = new Date().getTime();

        if (time - currentTime < 86400000) {
            return "Today at " + convertLongToTime(time);
        } else if (time - currentTime < (2 * 86400000)) {
            return "Tomorrow at " + convertLongToTime(time);
        } else {
            return "at " + convertLongToTime(time) + " " + convertLongToDate(time);
        }
    }

    public String convertDateToTimeType(TimeType type, long startTime, long endTime) {
        switch (type) {
            case DAY: {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
                SimpleDateFormat amPmFormatter = new SimpleDateFormat("a");
                return simpleDateFormat.format(startTime) + "-" + simpleDateFormat.format(endTime) + amPmFormatter.format(endTime);
            }
            case WEEK: {
                SimpleDateFormat date= new SimpleDateFormat("d");
                return  date.format(startTime);
            }
            default:
                return null;
        }
    }
}
