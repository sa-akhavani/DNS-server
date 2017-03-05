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
                    System.out.println("searchType: " + jsonHandler.getSearchType());
                    System.out.println(line);
                    switch (type) {
                        case "search":
                            System.out.println("Search!");
                            String authServerName = jsonHandler.getRelatedAuthServer();
                            String authServerIp = findRelatedAuthServer(authServerName);
                            System.out.println("authIp:" + authServerIp);
                            if (authServerIp == null) {
                                sendResultToAgent(new Response("", true, false, 0).getRespObject());
                            } else if (jsonHandler.getSearchType().equals("iterative")) {
                                System.out.println(authServerIp);
                                sendResultToAgent(new Response(authServerIp, false, true, 0).getRespObject());
                            } else {
                                JSONObject domainIp = askAuthServerForDomainIp(authServerIp, line); //maybe null
                                System.out.println("domain name:" + domainIp.toString());
                                sendResultToAgent(domainIp);
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

    private void sendResultToAgent(JSONObject j) throws IOException {
        Transceiver t = new Transceiver(this.socket);
        t.send(j.toString() + '\n');
    }

    private JSONObject askAuthServerForDomainIp(String authServerPort, String message) throws IOException {
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