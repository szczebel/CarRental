package server.service;

import common.service.TestService;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestServiceImpl implements TestService {
    public String getServerInfo() {
        try {
            Thread.sleep(3000);
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
