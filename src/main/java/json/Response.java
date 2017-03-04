package json;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mnoroozi on 4/28/17.
 */
public class Response {
    private JSONObject respObject;
    private boolean isFinal;
    private boolean found;
    private String response;

    public Response(String line) throws JSONException {
        this.respObject = new JSONObject(line);
        this.isFinal = Boolean.parseBoolean(respObject.getString("isFinal"));
        this.found = Boolean.parseBoolean(respObject.getString("found"));
        this.response = respObject.getString("response");
        System.out.println(respObject.toString());
    }

    public Response(JSONObject jo) throws JSONException {
        this.respObject = jo;
        this.isFinal = Boolean.parseBoolean(respObject.getString("isFinal"));
        this.found = Boolean.parseBoolean(respObject.getString("found"));
        this.response = respObject.getString("response");
        System.out.println(respObject.toString());
    }

    public Response(String response, boolean isFinal, boolean found) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("response", response);
        jo.put("isFinal", isFinal);
        jo.put("found", found);
        this.respObject = jo;
        this.isFinal = Boolean.parseBoolean(respObject.getString("isFinal"));
        this.found = Boolean.parseBoolean(respObject.getString("found"));
        this.response = respObject.getString("response");
        System.out.println(respObject.toString());
    }

    public JSONObject getRespObject() {
        return respObject;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public boolean isFound() {
        return found;
    }


    public String getResponse() {
        return response;
    }
}
