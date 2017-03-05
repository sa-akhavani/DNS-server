package authdns;

import common.Server;
import common.Transceiver;
import json.Response;
import org.json.JSONException;
import org.json.JSONObject;
import root.JsonHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class AuthThread extends Thread {
    private ArrayList<Server> websites;
    private Socket socket;
    private JsonHandler jsonHandler;

    public AuthThread(Socket clientSocket, ArrayList<Server> websites) {
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
                            Server website = findWebsite(siteName);
                            if (website == null) {
                                sendResultToAgent(new Response("", true, false, 0).getRespObject());
                            } else {
                                System.out.println("sending back ip" + website.ip);
                                sendResultToAgent(new Response(website.ip, true, true, website.validTime).getRespObject());
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

    private Server findWebsite(String serverName) {
        for (Server s :
                websites) {
            System.out.println(s.name);
            if (s.name.equals(serverName)) {
                return s;
            }
        }
        return null;
    }

    private void sendResultToAgent(JSONObject j) throws IOException {
        Transceiver t = new Transceiver(this.socket);
        t.send(j.toString() + '\n');
    }
}