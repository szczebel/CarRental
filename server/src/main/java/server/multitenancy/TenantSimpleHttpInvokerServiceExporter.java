package server.multitenancy;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerServiceExporter;

import java.io.IOException;

public class TenantSimpleHttpInvokerServiceExporter extends SimpleHttpInvokerServiceExporter {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Headers requestHeaders = exchange.getRequestHeaders();
        String tenant = requestHeaders.getFirst("Tenant");
        if(tenant == null) {
            exchange.sendResponseHeaders(500, -1L);
        } else {
            CurrentTenantProvider.setTenantForTransaction(tenant);
            super.handle(exchange);
            CurrentTenantProvider.setTenantForTransaction(null);//todo: put it in finally?
        }
    }
}
