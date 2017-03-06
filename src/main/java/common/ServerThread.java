//package common;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//
///**
// * Created by MNoroozi on 06/03/2017.
// */
//public class ServerThread {
//
//    private void sendResultToAgent(JSONObject result) throws IOException {
//        Transceiver t = new Transceiver(this.socket);
//        t.send(result.toString() + '\n');
//    }
//
//    private JSONObject forwardMessage(String tldPort, String message) throws IOException {
//        Transceiver t = new Transceiver("localhost" , Integer.parseInt(tldPort));
//        t.send(message + '\n');
//        try {
//            return new JSONObject(t.receive());
//        } catch (JSONException e) {
//            return null;
//        }
//    }
//}
