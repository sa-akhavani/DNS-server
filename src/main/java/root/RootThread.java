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
                    JSONObject response = null;
                    switch (type) {
                        case "search":
                            response = search(jsonHandler);
                            break;
                        case "add":
                            response = add(jsonHandler);
                            break;
                        case "update":
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
        String tldName = jsonHandler.getRelatedTld();
        String tldIp = findRelatedTld(tldName);
        if (tldIp == null) {
           return new Response("", true, false, 0).getRespObject();
        } else if (jsonHandler.getSearchType().equals("iterative")) {
            return new Response(tldIp, false, true, 0).getRespObject();
        } else {
            return forwardMessage(tldIp, jsonHandler.getFullMessage()); //maybe null
        }
    }

    private JSONObject add(JsonHandler jsonHandler) throws JSONException, IOException {
        String tldIp = findRelatedTld(jsonHandler.getRelatedTld());
        if (tldIp == null) {
            return new Response("", true, false, 0).getRespObject();
        }  else {
            return forwardMessage(tldIp, jsonHandler.getFullMessage());
        }
    }

    private JSONObject update(JsonHandler jsonHandler) throws JSONException, IOException {
        String tldIp = findRelatedTld(jsonHandler.getRelatedTld());
        if (tldIp == null) {
            return new Response("Error", true, false, 0).getRespObject();
        }  else {
            return forwardMessage(tldIp, jsonHandler.getFullMessage());
        }
    }

    private void sendResultToAgent(JSONObject result) throws IOException {
        Transceiver t = new Transceiver(this.socket);
        t.send(result.toString() + '\n');
    }

    private JSONObject forwardMessage(String tldPort, String message) throws IOException {
        Transceiver t = new Transceiver("localhost" , Integer.parseInt(tldPort));
        t.send(message + '\n');
        try {
            return new JSONObject(t.receive());
        } catch (JSONException e) {
            return null;
        }
    }

    private String findRelatedTld(String tldName) {
        for (Server s :
                tlds) {
            if (Objects.equals(s.name, tldName)) {
                System.out.println("tldName: " + tldName + "tldFound: " + s.name + " tldPort: " + s.ip);
                return Integer.toString(s.port); // TODO: 3/1/17 change port to ip if using mininet
            }
        }
        return null;
    }
}
