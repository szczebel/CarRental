package server.service;

import common.service.TestService;
import org.springframework.stereotype.Service;
import server.multitenancy.CurrentTenantProvider;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service("testService")
public class TestServiceImpl implements TestService {
    public String getServerInfo() {
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            String tenantForTransaction = CurrentTenantProvider.getTenantForTransaction();
            return tenantForTransaction + " @ " + hostAddress;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
