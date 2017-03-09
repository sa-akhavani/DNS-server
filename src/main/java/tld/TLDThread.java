package tld;

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
import java.util.Objects;

/**
 * Created by ali on 3/1/17.
 */
public class TLDThread extends Thread {
    private ArrayList<Server> authServers;
    private Socket socket;
    private JsonHandler jsonHandler;

    public TLDThread(Socket clientSocket, ArrayList<Server> authServers) {
        this.socket = clientSocket;
        this.authServers = authServers;
        jsonHandler = new JsonHandler();
    }

    public void run() {
        System.out.println("New Thread!");
        String line;
        while (true) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while ((line = br.readLine()) != null) {
                    String type = jsonHandler.parseCommand(line);
                    JSONObject response = null;
                    System.out.println(line);
                    switch (type) {
                        case "search":
                            System.out.println("Search!");
                            response = search(jsonHandler);
                            break;
                        case "add":
                            System.out.println("Add");
                            response = add(jsonHandler);
                            break;
                        case "update":
                            System.out.println("Update");
                            response = add(jsonHandler);
                            break;
                        case "wrong":
                            System.out.println("Bad Input");
                            break;
                    }
                    if(response != null)
                        sendResultToAgent(response);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            break;
        }
        System.out.println("Thread Ended!");
    }

    private JSONObject search(JsonHandler jsonHandler) throws JSONException, IOException {
        String tldIp = findRelatedAuthServer(jsonHandler.getRelatedAuthServer());
        if (tldIp == null) {
            return new Response(" ", true, false, 0).getRespObject();
        } else if (jsonHandler.getSearchType().equals("iterative")) {
            return new Response(tldIp, false, true, 0).getRespObject();
        } else {
            return forwardMessage(tldIp, jsonHandler.getFullMessage()); //maybe null
        }
    }

    private JSONObject add(JsonHandler jsonHandler) throws JSONException, IOException {
        String tldIp = findRelatedAuthServer(jsonHandler.getRelatedAuthServer());
        if (tldIp == null) {
            return new Response("Error", true, false, 0).getRespObject();
        }  else {
            return forwardMessage(tldIp, jsonHandler.getFullMessage());
        }
    }

    private void sendResultToAgent(JSONObject j) throws IOException {
        Transceiver t = new Transceiver(this.socket);
        t.send(j.toString() + '\n');
    }

    private JSONObject forwardMessage(String authServerPort, String message) throws IOException {
        Transceiver t = new Transceiver("localhost" , Integer.parseInt(authServerPort));
        t.send(message + '\n');
        try {
            return new JSONObject(t.receive());
        } catch (JSONException e) {
            return null;
        }
    }

    private String findRelatedAuthServer(String authServerName) {
        for (Server s :
                authServers) {
            System.out.println("finding: " + authServerName + " this time: " + s.name) ;
            if (Objects.equals(s.name, authServerName)) {
                return Integer.toString(Integer.parseInt(s.ip)); // TODO: 3/1/17 change port to ip if using mininet
            }
        }
        return null;
    }

}