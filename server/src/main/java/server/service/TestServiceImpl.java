package server.service;

import common.service.TestService;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service("testService")
public class TestServiceImpl implements TestService {
    public String getServerInfo() {
        try {
            Thread.sleep(3000);
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
