package agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by ali on 2/28/17.
 */
public class UserThread extends Thread{
    private Socket socket;
    String request;
    public UserThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        System.out.println("New Thread!");
        String line;
        while (true) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            break;
        }

        System.out.println("Thread Ended!");
    }
}
