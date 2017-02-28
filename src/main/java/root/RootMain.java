package root;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ali on 2/28/17.
 */
public class RootMain {
    public static void main(String[] args) throws IOException {
        ServerSocket myServer = new ServerSocket(12344);
        while (true) {
            Socket clientSocket = myServer.accept();
            RootThread rt = new RootThread(clientSocket);
            rt.start();
        }
    }
}
