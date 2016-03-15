package common.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ServerInfo implements Serializable {
    private final String tenant;
    private final String hostAddress;
    private final ZonedDateTime serverTimestamp;

    public ServerInfo(String tenant, String hostAddress, ZonedDateTime serverTimestamp) {
        this.tenant = tenant;
        this.hostAddress = hostAddress;
        this.serverTimestamp = serverTimestamp;
    }

    @Override
    public String toString() {
        return
                tenant + " @ " + hostAddress + ", time: " + serverTimestamp.format(DateTimeFormatter.ofPattern("dd.MM.yyy '@' HH:mm"));
    }
}
