package mocks;

import common.domain.ServerInfo;
import common.service.ServerInfoService;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component("serverInfoService")
public class MockServerInfoService implements ServerInfoService {
    @Override
    public ServerInfo getServerInfo() {
        return new ServerInfo("???", "mock server", ZonedDateTime.now());
    }
}
