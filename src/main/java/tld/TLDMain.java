package tld;

import common.Server;
import root.RootThread;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by ali on 3/1/17.
 */
public class TLDMain {
    public static void main(String[] args) throws IOException {
        ArrayList<Server> websites = new ArrayList<>();
        ServerSocket myServer = new ServerSocket(12330);
        configTLD("./resources/root1.tld.com", websites);
        while (true) {
            Socket clientSocket = myServer.accept();
            System.out.println("new connection");
            TLDThread rt = new TLDThread(clientSocket, websites);
            rt.start();
        }
    }

    private static void configTLD(String fileName, ArrayList<Server> roots) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        while((line = br.readLine())!= null) {
            String[] args = line.split(" ");
            Server rs = new Server(args[0], args[1], 0);
            roots.add(rs);
        }
    }
}
