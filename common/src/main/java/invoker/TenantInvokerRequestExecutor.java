package invoker;

import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;

import java.io.IOException;
import java.net.HttpURLConnection;

public class TenantInvokerRequestExecutor extends SimpleHttpInvokerRequestExecutor {

    @Override
    protected void prepareConnection(HttpURLConnection connection, int contentLength) throws IOException {
        super.prepareConnection(connection, contentLength);
        connection.setRequestProperty("tenant", "National");
    }
}
