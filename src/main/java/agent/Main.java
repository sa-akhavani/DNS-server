package agent;

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
public class Main {
    public static void main(String[] args) throws IOException {
        ArrayList<Server> roots = new ArrayList<>();
        ServerSocket myServer = new ServerSocket(12345);
        configAgent("./resources/root-servers", roots);


        System.out.println("agent started");

        while (true) {
            Socket clientSocket = myServer.accept();
            UserThread ut = new UserThread(clientSocket, roots);
            ut.start();
        }

    }

    private static void configAgent(String fileName, ArrayList<common.Server> roots) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        while((line = br.readLine())!= null) {
            String[] args = line.split("\\s");
            Server rs = new Server(args[0], "localhost", Integer.parseInt(args[1]));
            roots.add(rs);
        }
    }
}
