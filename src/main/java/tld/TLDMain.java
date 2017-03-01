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
        while (true) {
            Socket clientSocket = myServer.accept();
            TLDThread rt = new TLDThread(clientSocket, websites);
            rt.start();
        }
    }

    private static void configTLD(String fileName, ArrayList<Server> roots) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        while((line = br.readLine())!= null) {
            String[] args = line.split("\\s");
            Server rs = new Server(args[0], "localhost", Integer.parseInt(args[1]));
            roots.add(rs);
        }
    }
}
