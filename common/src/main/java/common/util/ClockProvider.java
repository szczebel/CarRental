package common.util;


import java.time.Clock;
import java.time.ZoneId;
import java.util.function.Supplier;

public class ClockProvider implements Supplier<Clock> {

    public static final Clock SystemClock = Clock.system(ZoneId.systemDefault());
    private Clock clock = SystemClock;

    @Override
    public Clock get() {
        return clock;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    public void resetToSystem() {
        setClock(SystemClock);
    }
}
