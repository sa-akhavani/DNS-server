package common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by ali on 2/28/17.
 */
public class Transceiver {
    private Socket socket;

    public Transceiver(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
    }

    public Transceiver(Socket s) {
        this.socket = s;
    }

    public void send(String data) throws IOException {
        PrintWriter output = new PrintWriter(socket.getOutputStream());
        output.write(data);
        output.flush();
//        output.close();
    }

    public String receive() throws IOException {
        String res = "";
        BufferedReader serverReply = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String response;
        while ((response = serverReply.readLine()) != null) {
            res+= response;
            res+= '\n';
//            System.out.println("data from server: " + response);
            if(!serverReply.ready())
                break;
        }
        return res;
//        serverReply.close();
    }

    public void close() throws IOException {
        socket.close();
    }
}
