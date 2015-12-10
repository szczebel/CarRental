package common.util;


import java.time.ZonedDateTime;
import java.util.function.Supplier;

public class FreezableClock implements Supplier<ZonedDateTime> {

    private ZonedDateTime frozenTime;

    @Override
    public ZonedDateTime get() {
        return frozenTime != null ? frozenTime : ZonedDateTime.now();
    }

    public void freezeTime(ZonedDateTime dateTime) {
        frozenTime = dateTime;
    }

    public void unfreze() {
        frozenTime = null;
    }
}
