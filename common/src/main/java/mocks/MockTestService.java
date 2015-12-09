package mocks;

import common.service.TestService;
import org.springframework.stereotype.Component;

@Component("testService")
public class MockTestService implements TestService {
    @Override
    public String getServerInfo() {
        return "I'm a mock";
    }
}
