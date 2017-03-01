package common;

/**
 * Created by ali on 3/1/17.
 */
public class Server {
    public String name;
    public String ip;
    public int port;

    public Server(String name, String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.name = name;
    }
}
