package agent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ali on 2/28/17.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        ServerSocket myServer = new ServerSocket(12345);
        while (true) {
            Socket clientSocket = myServer.accept();
            UserThread ut = new UserThread(clientSocket);
            ut.start();
        }
    }
}
