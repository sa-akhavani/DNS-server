package root;

import common.Server;
import common.Transceiver;
import org.json.JSONException;

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
    private String searchType;

    public RootThread(Socket socket, ArrayList<Server> tlds, String searchType) {
        this.socket = socket;
        this.tlds = tlds;
        this.searchType = searchType;
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
                    switch (type) {
                        case "search":
                            System.out.println("Search!");
                            String tldName = jsonHandler.getRelatedTld();
                            String tldIp = findRelatedTld(tldName);
                            if (tldIp == null) {
                                // TODO: 3/1/17 Not Found!
                            } else if (searchType.equals("iterative")) {
//                                Handle Iterative Root Server
                                sendResultToAgent(tldIp);
                                // TODO: 3/1/17 need to send a json that says it is iterative
                            } else {
//                                Handle Recursive Root Server
                                String domainIp = askTldForDomainIp();
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

    private String askTldForDomainIp() {
        return "";
    }

    private void sendResultToAgent(String tldIp) throws IOException {
        Transceiver t = new Transceiver(this.socket);
        t.send(tldIp + '\n');
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
