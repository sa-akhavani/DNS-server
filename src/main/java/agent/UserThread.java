package agent;

import common.Server;
import common.Transceiver;
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
    private String searchType;


    UserThread(Socket socket, ArrayList<Server> roots, String searchType) {
        this.socket = socket;
        reqHandler = new RequestHandler(searchType);
        this.roots = roots;
        this.searchType = searchType;
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
                            String response = handleSearch(jo);
                            sendResultToUser(response);
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

    private String handleSearch(JSONObject jo) throws IOException {
        if(searchType.equals("iterative"))
            return iterativeSearch(jo);
        else
            return defaultSearch(jo);
    }

    private String defaultSearch(JSONObject jo) throws IOException {
        System.out.println("recursive search!!!");
        String response = "";
        for (Server root :
                roots) {
            Transceiver agent = new Transceiver(root.ip, root.port);
            agent.send(jo.toString() + '\n');
            response = agent.receive();
            if(response.equals(""))
                break;
        }

        return response;
    }

    private String iterativeSearch(JSONObject jo) throws IOException {
        System.out.println("iterative search!!!");

        String response = defaultSearch(jo);

        if(!response.equals("")) {
            System.out.println("hahaha");
            Transceiver tld = new Transceiver("localhost", Integer.parseInt(response));
            tld.send(jo.toString() + '\n');
            response = tld.receive();
        }

        return response;
    }

    private void sendResultToUser(String websiteIP) throws IOException {
        Transceiver t = new Transceiver(this.socket);
        t.send(websiteIP + '\n');
    }
}
