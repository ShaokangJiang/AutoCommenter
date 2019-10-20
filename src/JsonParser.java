

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 */
public class JsonParser {

  public static String parseIatResult(String json, ArrayList<String> value) {
    StringBuffer ret = new StringBuffer();
    StringBuffer re = new StringBuffer();
    try {
      JSONTokener tokener = new JSONTokener(json);
      JSONObject joResult = new JSONObject(tokener);

      JSONArray words = joResult.getJSONArray("ws");
      for (int i = 0; i < words.length(); i++) {
        JSONArray items = words.getJSONObject(i).getJSONArray("cw");
        JSONObject obj = items.getJSONObject(0);
        ret.append(obj.getString("w"));
      }
      if(joResult.getString("pgs").equals("apd")) {
        value.add(ret.toString());
      }else if(joResult.getString("pgs").equals("rpl")) {
        JSONArray range = joResult.getJSONArray("rg");
        for(int i=range.getInt(0)-1;i<range.getInt(1);i++) {
          value.set(i, null);
        }
        value.add(ret.toString());
      }
      for(String a:value) if(a!=null) re.append(a);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return re.toString();
  }
  
  public static String parseTranslateResult(String json) {
    StringBuffer ret = new StringBuffer();
    try {
      JSONTokener tokener = new JSONTokener(json);
      JSONObject joResult = new JSONObject(tokener);

      JSONArray words = joResult.getJSONArray("trans_result");
      for (int i = 0; i < words.length(); i++) {
        ret.append(words.getJSONObject(i).getString("dst"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ret.toString();
  }

}
