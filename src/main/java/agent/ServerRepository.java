package agent;

import common.Server;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Created by MNoroozi on 06/03/2017.
 */
public class ServerRepository {
    private ArrayList<Server> storage;
    final static Logger logger = Logger.getLogger(ServerRepository.class);

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
        if (logger.isDebugEnabled()) {
            logger.debug("Reserve Record Added -> " + s.name);
        }
    }

    public boolean contains(String websiteName) {
        for (Server s : storage)
            if (s.name == websiteName)
                return checkValid(s);

        return false;
    }

    public Server get(String websiteName) {
        for (Server s : storage)
            if (s.name == websiteName)
                if (checkValid(s))
                    return s;
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

}