package root;

import common.Server;
import common.Transceiver;
import json.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by ali on 2/28/17.
 */
public class RootThread extends Thread {
    private ArrayList<Server> tlds;
    private Socket socket;
    private JsonHandler jsonHandler;

    public RootThread(Socket clientSocket, ArrayList<Server> tlds) {
        this.socket = clientSocket;
        this.tlds = tlds;
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
                    switch (type) {
                        case "search":
                            System.out.println("Search!");
                            String tldName = jsonHandler.getRelatedTld();
                            String tldIp = findRelatedTld(tldName);
                            if (tldIp == null) {
                                sendResultToAgent(new Response("", true, false).getRespObject());
                            } else if (jsonHandler.getSearchType().equals("iterative")) {
                                System.out.println(tldIp);
                                sendResultToAgent(new Response(tldIp, false, true).getRespObject());
                            } else {
                                JSONObject domainIp = askTldForDomainIp(tldIp, line); //maybe null
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

    private JSONObject askTldForDomainIp(String tldPort, String message) throws IOException {
        Transceiver t = new Transceiver("localhost" , Integer.parseInt(tldPort));
        t.send(message + '\n');
        try {
            return new JSONObject(t.receive());
        } catch (JSONException e) {
            return null;
        }
    }

    private void sendResultToAgent(JSONObject agentIP) throws IOException {
        Transceiver t = new Transceiver(this.socket);
        t.send(agentIP.toString() + '\n');
    }

    private String findRelatedTld(String tldName) {
        for (Server s :
                tlds) {
            if (Objects.equals(s.name, tldName)) {
                return Integer.toString(s.port); // TODO: 3/1/17 change port to ip if using mininet
            }
        }
        return null;
    }
}
