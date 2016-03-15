package server.service;

import common.domain.ServerInfo;
import common.service.ServerInfoService;
import org.springframework.stereotype.Service;
import server.multitenancy.CurrentTenantProvider;
import server.multitenancy.RequiresTenant;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;

@Service("serverInfoService")
public class ServerInfoServiceImpl implements ServerInfoService {

    @RequiresTenant
    public ServerInfo getServerInfo() {
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            String tenantForTransaction = CurrentTenantProvider.getTenantForTransaction();
            return new ServerInfo(tenantForTransaction, hostAddress, ZonedDateTime.now());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
