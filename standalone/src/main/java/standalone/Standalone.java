package standalone;

import client.GUI;
import server.Server;

public class Standalone {

    public static void main(String[] args) {
        Server.main(args);
        GUI.main(args);
    }
}
