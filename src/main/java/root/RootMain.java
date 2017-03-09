package root;

import common.Server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by ali on 2/28/17.
 */
public class RootMain {
    public static void main(String[] args) throws IOException {
        ArrayList<Server> tlds = new ArrayList<>();
        ServerSocket myServer = new ServerSocket(12340);
        configAgent("./resources/root1.tlds", tlds);

        System.out.println("root1 started");
        while (true) {
            Socket clientSocket = myServer.accept();
            RootThread rt = new RootThread(clientSocket, tlds);
            rt.start();
        }
    }


    private static void configAgent(String fileName, ArrayList<Server> tlds) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        while((line = br.readLine())!= null) {
            String[] args = line.split("\\s");
            Server tld = new Server(args[0], "localhost", Integer.parseInt(args[1]));
            tlds.add(tld);
        }
        return;
    }
}
