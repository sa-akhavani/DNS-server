package tld;

import common.Server;

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
    private static int port;
    private static String fileName;

    public static void main(String[] args) throws IOException {
        ArrayList<Server> websites = new ArrayList<>();
        initiateTLD(args);

        ServerSocket myServer = new ServerSocket(port);
        configTLD("./resources/root1.tld." + fileName, websites);

        System.out.println("tld " + fileName + " started");
        while (true) {
            Socket clientSocket = myServer.accept();
            System.out.println("new connection");
            TLDThread rt = new TLDThread(clientSocket, websites);
            rt.start();
        }
    }

    private static void initiateTLD(String[] args) {
        if(args.length != 1)
            System.exit(0);

        if(args[0].equals("1")) {
            port = 20001;
            fileName = "com";
        } else if(args[0].equals("2")) {
            port = 20002;
            fileName = "org";
        } else
            System.exit(0);
    }

    private static void configTLD(String fileName, ArrayList<Server> roots) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        while((line = br.readLine())!= null) {
            String[] args = line.split(" ");
            System.out.println(args[0] + " " + args[1]);
            Server rs = new Server(args[0], args[1], 0);
            roots.add(rs);
        }
    }
}
