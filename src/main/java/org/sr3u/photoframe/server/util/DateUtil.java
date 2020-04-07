package org.sr3u.photoframe.server.util;

import com.google.protobuf.Timestamp;
import org.sr3u.photoframe.server.Server;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
    public static Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    public static long timestamp(Date d) {
        return d.getTime() / 1000;
    }

    public static boolean isDateExpiredForMediaItem(Date eventQueryTimestamp) {
        return eventQueryTimestamp == null || (Math.abs((new Date().getTime() - eventQueryTimestamp.getTime()) / 1000) > Server.settings.getMedia().mediaItemExpiryTime);
    }

    public static com.google.type.Date toProtobuf(Date date) {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTime(date);
        com.google.type.Date.Builder dateBuilder = com.google.type.Date.newBuilder();
        dateBuilder.setDay(cal.get(Calendar.DAY_OF_MONTH));
        // Months start at 0, not 1
        dateBuilder.setMonth(cal.get(Calendar.MONTH) + 1);
        dateBuilder.setYear(cal.get(Calendar.YEAR));
        return dateBuilder.build();
    }

    public static Date getCreationDate(Timestamp creationTime) {
        return new Date(creationTime.getSeconds() * 1000);
    }
}
