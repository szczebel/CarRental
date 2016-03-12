package webgui.controller;

public class TestResponse {
    String requestor;
    String serverResponse;
    String handshake;

    public TestResponse(String requestor, String serverResponse, String handshake) {
        this.requestor = requestor;
        this.serverResponse = serverResponse;
        this.handshake = handshake;
    }

    public String getRequestor() {
        return requestor;
    }

    public String getServerResponse() {
        return serverResponse;
    }

    public String getHandshake() {
        return handshake;
    }
}
