package json;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ali on 2/28/17.
 */
public class RequestHandler {
    private String searchType;
    private String request;
    private String type;
    private String ip;


    public String getRequest() {
        return request;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIp() {
        return ip;
    }

    public String getSearchType() {
        return searchType;
    }

    public String parseType(String line) {
        this.request = line;
        String[] args = request.split("\\s");

        if (args[0].equals("a") && args.length == 3) {
            this.type = "add";
            this.request = args[1];
            this.ip = args[2];
            return "add";
        } else if (args[0].equals("u") && args.length == 3) {
            this.type = "update";
            this.request = args[1];
            this.ip = args[2];
            return "update";
        } else if (args[0].equals("s") && args.length == 3) {
            this.type = "search";
            this.searchType = args[2];
            this.request = args[1];
            return "search";
        }
        type = "wrong";
        return "wrong";
    }

    public JSONObject createSearchJson() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("domain", request);
        jo.put("hour", getCurrentHourType());
        jo.put("type", "search");
        jo.put("searchType", searchType);
        return jo;
    }

    private String getCurrentHourType() {
        DateTime dt = new DateTime();
        int hour = dt.getHourOfDay();
        String hourType = "noon";
        if (hour >= 1 && hour <= 6)
            hourType = "dawn";
        else if (hour > 6 && hour <= 11)
            hourType = "morning";
        else if (hour > 11 && hour <= 15)
            hourType = "noon";
        else if (hour > 15 && hour <= 19)
            hourType = "afternoon";
        else if (hour > 19 && hour <= 0)
            hourType = "night";
        return hourType;
    }

    public JSONObject createAddJson() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("domain", request);
        jo.put("ip" , ip);
        jo.put("type", "add");
        return jo;
    }

    public JSONObject createUpdateJson() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("domain", request);
        jo.put("ip" , ip);
        jo.put("type", "update");
        return jo;
    }
}
