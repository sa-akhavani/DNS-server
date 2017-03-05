package common;

/**
 * Created by ali on 3/1/17.
 */
public class Server {
    public String name;
    public String ip;
    public int port;
    public int validTime;
    private long expirationTime;


    public Server(String name, String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.name = name;
        this.validTime = 0;
    }

    public Server(String name, String ip, int port, int validTime) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.validTime = validTime;
    }

    public boolean expired() {
        if (System.currentTimeMillis() > this.expirationTime)
            return true;
        else
            return false;
    }

    public void setExpirationTime() {
        this.expirationTime = System.currentTimeMillis() + validTime*1000;
    }
}
