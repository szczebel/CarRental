package webgui.controller;

public class TestRequest {
    String handshake;

    public String getHandshake() {
        return handshake;
    }

    public void setHandshake(String handshake) {
        this.handshake = handshake;
    }

    @Override
    public String toString() {
        return "TestRequest{" +
                "handshake='" + handshake + '\'' +
                '}';
    }
}
