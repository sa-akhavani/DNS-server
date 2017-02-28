package agent;

import root.RootServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by ali on 2/28/17.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        ArrayList<RootServer> roots = new ArrayList<>();

        ServerSocket myServer = new ServerSocket(12345);
        RootServer rs = new RootServer("localhost", 12344);
        roots.add(rs);

        while (true) {
            Socket clientSocket = myServer.accept();
            UserThread ut = new UserThread(clientSocket, roots);
            ut.start();
        }

    }
}
