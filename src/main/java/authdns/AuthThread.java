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
        System.out.println("Auth Thread!");
        String line;
        while (true) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                    String type = jsonHandler.parseCommand(line);
                    JSONObject response = null;
                    switch (type) {
                        case "search":
                            System.out.println("Search!");
                            response = search(jsonHandler);
                            break;
                        case "add":
                            System.out.println("Add!");
                            response = add(jsonHandler);
                            break;
                        case "update":
                            System.out.println("Update");
                            response = update(jsonHandler);
                            break;
                        case "wrong":
                            System.out.println("Bad Input");
                            break;
                    }
                    if(response != null)
                        sendResultToAuth(response);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            break;
        }
        System.out.println("Thread Ended!");
    }

    private JSONObject search(JsonHandler jsonHandler) throws JSONException, IOException {
        Server website = findWebsite(jsonHandler.getDomain());
        if (website == null) {
            return new Response("", true, false, 0).getRespObject();
        } else {
            System.out.println("sending back ip" + website.ip);
            return new Response(website.ip, true, true, website.validTime).getRespObject();
        }
    }

    private JSONObject add(JsonHandler jsonHandler) throws JSONException, IOException {
        if(addWebsite(jsonHandler.getDomain(), jsonHandler.getIP()))
            return new Response("Success", true, true, 0).getRespObject();
        else
            return new Response("Error", true, false, 0).getRespObject();
    }

    private JSONObject update(JsonHandler jsonHandler) throws JSONException {
        if(updateWebsite(jsonHandler.getDomain(), jsonHandler.getIP()))
            return new Response("Success", true, true, 0).getRespObject();
        else
            return new Response("Error", true, false, 0).getRespObject();
    }

    private boolean contains(String domain) {
        for (Server s: websites) {
            if (s.name.equals(domain))
                return true;
        }
        return false;
    }
    private boolean addWebsite(String domain, String ip) {
        if(contains(domain))
            return false;

        websites.add(new Server(domain, ip, 0, 50));
        return true;
    }

    private boolean updateWebsite(String domain, String ip) {
        for (Server s: websites) {
            if(s.name.equals(domain)) {
                s.ip = ip;
                return true;
            }
        }

        return false;
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

    private void sendResultToAuth(JSONObject j) throws IOException {
        Transceiver t = new Transceiver(this.socket);
        t.send(j.toString() + '\n');
    }
}