package common.util;

import java.time.ZonedDateTime;

public class TimeUtils {
    public static ZonedDateTime toMidnight(ZonedDateTime time) {
        time = time.minusNanos(time.getNano());
        time = time.minusSeconds(time.getSecond());
        time = time.minusMinutes(time.getMinute());
        time = time.minusHours(time.getHour());
        return time;
    }
}
