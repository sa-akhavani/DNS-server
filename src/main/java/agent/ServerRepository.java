package agent;

import common.Server;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Created by MNoroozi on 06/03/2017.
 */
public class ServerRepository {
    private ArrayList<Server> storage;

    private static ServerRepository serverRepository = new ServerRepository();

    public static ServerRepository getRepository() {
        return serverRepository;
    }

    private ServerRepository() {
        storage = new ArrayList<>();
    }

    public void store(Server s) {
        s.setExpirationTime();
        storage.add(s);
        System.out.println("server " + s.name + " stored.");
    }

    public boolean contains(String websiteName) {
        for (Server s : storage)
            if (s.name.equals(websiteName))
                return checkValid(s);

        return false;
    }

    public Server get(String websiteName) {
        for (Server s : storage)
            if (s.name.equals(websiteName))
                if (checkValid(s)){
                    System.out.println(s.name + " is catched - remaining time: " + s.getRemainingTime());
                    return s;
                }
                else
                    return null;

        return null;
    }

    private boolean checkValid(Server s) {
        if (s.expired()) {
            storage.remove(s);
            return false;
        } else
            return true;
    }

    public void remove(String request) {
        Server s = get(request);
        if(s != null)
            storage.remove(s);
    }
}