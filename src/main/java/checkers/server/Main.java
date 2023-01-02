package checkers.server;

import javafx.application.Platform;

public class Main {
    public static void main(String[] args) {
        Platform.startup(() -> {
            Server server = new Server();
            if(!server.run()) {
                System.out.println("error: failed to start the server");
            }
        });
    }
}
