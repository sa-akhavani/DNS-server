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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class AuthThread extends Thread {
    private ArrayList<Server> websites;
    private Socket socket;
    private JsonHandler jsonHandler;
    private String fileName;

    public AuthThread(Socket clientSocket, ArrayList<Server> websites, String fileName) {
        this.socket = clientSocket;
        this.websites = websites;
        this.fileName = fileName;
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
        if(addWebsite(jsonHandler.getDomain(), jsonHandler.getIP(), jsonHandler.getValidTime()))
            return new Response("Success", true, true, 0).getRespObject();
        else
            return new Response("Error", true, false, 0).getRespObject();
    }

    private JSONObject update(JsonHandler jsonHandler) throws JSONException, IOException {
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
    private boolean addWebsite(String domain, String ip, int validTime) {
        if(contains(domain))
            return false;

        websites.add(new Server(domain, ip, 0, validTime));
        appendFile('\n' + domain + " " + ip +" " + validTime);
        return true;
    }

    private void appendFile(String newLine) {
        System.out.println("appending o to file: " + fileName + " newLine: " + newLine);
        try {
            Files.write(Paths.get(fileName), newLine.getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }

    private boolean updateWebsite(String domain, String ip) throws IOException {
        for (Server s: websites) {
            if(s.name.equals(domain)) {
                s.ip = ip;
                changeFileLine(s.name, s.name + " " + s.ip + " " + s.validTime);
                return true;
            }
        }
        return false;
    }
    

    private void changeFileLine(String oldLine, String newLine) throws IOException {
        Path filePath = Paths.get(fileName);
        List<String> fileContent = new ArrayList<>(Files.readAllLines(filePath, StandardCharsets.UTF_8));

        for (int i = 0; i < fileContent.size(); i++) {
            if (fileContent.get(i).contains(oldLine)) {
                fileContent.set(i, newLine);
                break;
            }
        }

        Files.write(filePath, fileContent, StandardCharsets.UTF_8);
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