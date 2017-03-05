package authdns;


import common.Server;
import root.RootThread;
import tld.TLDThread;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by MNoroozi on 4/1/17.
 */

public class AuthMain {
    private static int port;
    private static String fileName;
    public static void main(String[] args) throws IOException {
        ArrayList<Server> websites = new ArrayList<>();
        initiateAuthServer(args);

        ServerSocket myServer = new ServerSocket(port);
        configAuth("./resources/root1.tld." + fileName, websites);

        while (true) {
            Socket clientSocket = myServer.accept();
            System.out.println("new connection");
            AuthThread rt = new AuthThread(clientSocket, websites);
            rt.start();
        }
    }

    private static void initiateAuthServer(String[] args) {
        if(args.length != 1)
            System.exit(0);

        if(args[0].equals("1")) {
            port = 30001;
            fileName = "com.google";
        } else if(args[0].equals("2")) {
            port = 30002;
            fileName = "com.bing";
        }else if(args[0].equals("3")) {
            port = 30003;
            fileName = "org.nodet";
        }else if(args[0].equals("4")) {
            port = 30004;
            fileName = "org.acm";
        } else
            System.exit(0);
    }


    private static void configAuth(String fileName, ArrayList<Server> roots) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        while((line = br.readLine())!= null) {
            String[] args = line.split(" ");
            System.out.println(args[0] + " " + args[2]);
            Server rs = new Server(args[0], args[1], 0, Integer.parseInt(args[2]));
            roots.add(rs);
        }
    }
}
