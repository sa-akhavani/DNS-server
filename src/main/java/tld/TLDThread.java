package tld;

import common.Server;
import common.Transceiver;
import org.json.JSONException;
import root.JsonHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by ali on 3/1/17.
 */
public class TLDThread extends Thread {
    private ArrayList<Server> websites;
    private Socket socket;
    private JsonHandler jsonHandler;

    public TLDThread(Socket clientSocket, ArrayList<Server> websites) {
        this.socket = clientSocket;
        this.websites = websites;
        jsonHandler = new JsonHandler();
    }

    public void run() {
        System.out.println("New Thread!");
        String line;
        while (true) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                    String type = jsonHandler.parseCommand(line);
                    switch (type) {
                        case "search":
                            System.out.println("Search!");
                            String siteName = jsonHandler.getDomain();
                            String websiteIP = findWebsiteIP(siteName);
                            if (websiteIP == null) {
                                // TODO: 3/1/17 Not Found!
                            } else {
                                System.out.println("sending back ip" + websiteIP);
//                                Handle Iterative Root Server
                                sendResultToAgent(websiteIP);
                                // TODO: 3/1/17 need to send a json that says it is iterative
                            }

                            break;
                        case "add":
                            System.out.println("Add");
                            break;
                        case "update":
                            System.out.println("Update");
                            break;
                        case "wrong":
                            System.out.println("Bad Input");
                            break;
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            break;
        }
        System.out.println("Thread Ended!");
    }

    private String findWebsiteIP(String serverName) {
        for (Server s :
                websites) {
            System.out.println(s.name);
            if (s.name.equals(serverName)) {
                return s.ip;
            }
        }
        return null;
    }

    private void sendResultToAgent(String resultIP) throws IOException {
        Transceiver t = new Transceiver(this.socket);
        t.send(resultIP + '\n');
    }

}