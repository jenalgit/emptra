package Utils;

import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.List;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    // constructor
    public JSONParser() {

    }

    // function get json from url
    // by making HTTP POST or GET mehtod
    public JSONObject makeHttpRequest(String url,
                                      String method,
                                      List<NameValuePair> params,
                                      List<NameValuePair> headers,
                                      String data) {
        // Making HTTP request
        String contentType = "";
        int i = 0;
        try {
            // check for request method
            if (method == "POST") {
                // request method is POST
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF_8"));
                while(i < headers.size()){
                    NameValuePair hd = headers.get(i);
                    httpPost.setHeader(hd.getName(), hd.getValue());
                    i++;
                }

                StringEntity se = new StringEntity(data);
                httpPost.setEntity(se);

                try {

                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();
                    Log.d("POST ", "Status Code : " + httpResponse.getStatusLine().getStatusCode());

                    Header[] content = httpResponse.getHeaders("Content-Type");
                    contentType = content[0].getValue();
                    Log.d("POST (Content-Type) ", "" + contentType);
                }
                catch(Exception e){
                    Log.e("POST", "Failed : "+e.getMessage());
                    return null;
                }
//              Get Header Data from the HTTP Response
//                int i = 0;
//                Header[] h = httpResponse.getAllHeaders();
//                while(i< h.length){
//                    Log.d("POST ", h[i].getName() + h[i].getValue());
//                    i++;
//                }

            } else if (method == "GET") {
                // request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
                Header[] content = httpResponse.getHeaders("Content-Type");
                contentType = content[0].getValue();
                Log.d("GET (Content-Type)", "" + contentType);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            if(contentType.equalsIgnoreCase("application/json")) {
                jObj = new JSONObject(json);
            }
            else
                jObj = new JSONObject("{}");
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }
}
