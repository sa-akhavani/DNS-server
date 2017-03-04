package agent;

import common.Server;
import common.Transceiver;
import json.RequestHandler;
import json.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by ali on 2/28/17.
 */
public class UserThread extends Thread {
    private ArrayList<Server> roots;
    private Socket socket;
    private RequestHandler reqHandler;


    UserThread(Socket socket, ArrayList<Server> roots) {
        this.socket = socket;
        this.reqHandler = new RequestHandler();
        this.roots = roots;
    }

    public void run() {
        System.out.println("New Thread!");
        String line;
        while (true) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while ((line = br.readLine()) != null) {

                    String type = reqHandler.parseType(line);
                    switch (type) {
                        case "search":
                            JSONObject jo = reqHandler.createSearchJson();
                            System.out.println(jo.toString());
                            Response response = handleSearch(jo);
                            sendResultToUser(response.getRespObject());
                            System.out.println(response);
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

    private Response handleSearch(JSONObject jo) throws IOException, JSONException {
        Response r = null;
        for (Server root: roots) {
            if(reqHandler.getSearchType().equals("iterative"))
                r = iterativeSearch(jo, root.port);
            else
                r =  defaultSearch(jo, root.port);

            if(r != null && r.isFinal())
                break;
        }

        return r;
    }

    private Response defaultSearch(JSONObject jo , int port) throws IOException {
        System.out.println("recursive search!!!");
        String response = "";
        Transceiver agent = new Transceiver("localhost", port);
        agent.send(jo.toString() + '\n');
        response = agent.receive();

        try {
            return new Response(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Response iterativeSearch(JSONObject jo , int port) throws IOException, JSONException {
        System.out.println("iterative search!!!");
        String response = "";
        Response resp = defaultSearch(jo, port);

        if(resp != null) {
            if(!resp.isFinal()) {
                return iterativeSearch(jo, Integer.parseInt(resp.getResponse()));
            } else if(!resp.isFound()) {
                return new Response("",true, false);
            }
        }

        return resp;
    }

    private void sendResultToUser(JSONObject response) throws IOException { //todo: not a good format
        Transceiver t = new Transceiver(this.socket);
        t.send(response.toString() + '\n');
    }
}
