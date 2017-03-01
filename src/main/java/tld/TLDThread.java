package tld;

import common.Server;
import root.JsonHandler;

import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by ali on 3/1/17.
 */
public class TLDThread extends Thread {
    private ArrayList<Server> websites;
    private Socket socket;
    private JsonHandler jsonHandler;

    public TLDThread(Socket clientSocket, ArrayList<Server> websites) {

    }
}
