package root;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ali on 3/1/17.
 */
public class JsonHandler {
    private JSONObject reqObject;
    private String type;
    private String line;
    private String relatedTld;

    public String parseCommand(String line) throws JSONException {
        this.line = line;
        this.reqObject = new JSONObject(line);
        this.type = reqObject.getString("type");
        System.out.println(type);
        return type;
    }

    public String getRelatedTld() throws JSONException {
        String domain = reqObject.getString("domain");
        String[] args = domain.split("\\.");
        relatedTld = args[args.length - 1];
        return relatedTld;
    }

    public String getDomain() throws JSONException {
        return reqObject.getString("domain");
    }

    public String getSearchType() throws JSONException {
        return reqObject.getString("searchType");
    }
}
