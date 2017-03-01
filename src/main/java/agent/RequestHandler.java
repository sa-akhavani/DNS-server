package agent;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ali on 2/28/17.
 */
public class RequestHandler {
    private String request;
    private String type;

    String getRequest() {
        return request;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String parseType(String line) {
        this.request = line;
        String[] args = request.split("\\s");
        if (args.length == 1) {
            type = "search";
            return "search";
        } else if (args[0].equals("add")) {
            type = "add";
            return "add";
        } else if (args[0].equals("update")) {
            type = "update";
            return "update";
        }
        type = "wrong";
        return "wrong";
    }

    JSONObject createSearchJson() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("domain", request);
        jo.put("hour", getCurrentHourType());
        jo.put("type", "search");
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
}
