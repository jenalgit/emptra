package Utils;

import java.io.*;

import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import JsonObject.Attendance;
import JsonObject.Employee;
import JsonObject.Month;
import JsonObject.Week;
import android.util.JsonReader;
import java.util.ArrayList;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import com.google.gson.Gson;

import static Utils.GenericUtils.*;

public class JsonProcessing extends Activity {

    public String jsonData;
    JsonReader reader;
    Context context;

    JsonProcessing(String fileName) throws FileNotFoundException {
        jsonData = "";
        BufferedReader reader;
        FileInputStream is = openFileInput(fileName);
        reader = new BufferedReader(new InputStreamReader(is));
        String line="";
        do {
            try {
                line = reader.readLine();
                jsonData = jsonData + line;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (line != null);
    }

    public JsonProcessing(Context c) {
        context = c;
    }

    public  <T> String convertObjToJson(T t) {
        Gson gson = new Gson();
        String jsonResult = gson.toJson(t);
        System.out.println(jsonResult);
        return jsonResult;
    }

    public <T> T convertJsonToObj(String fileName, Class<T> genericClassType, Boolean bExternalStorage) {
        Gson gson = new Gson();
        try {
            BufferedReader reader;

            if (bExternalStorage) {
                File sdCard = Environment.getExternalStorageDirectory();
                File directory = new File(sdCard.getAbsolutePath()
                        + "/emptra/json_data");
                File file = new File(directory, fileName);

                FileInputStream streamIn = new FileInputStream(file);
                reader = new BufferedReader(new InputStreamReader(streamIn));
            } else {
                reader = new BufferedReader(new InputStreamReader(context
                        .getAssets().open("json_data/"+ fileName), "UTF-8"));
            }
            //convert the json string back to object
            T obj = gson.fromJson(reader, genericClassType);

            LogMe("JsonProcessing", "Object : " + obj);
            return obj;
        } catch (IOException e) {
            LogMe("JsonProcessing", "JSON file not found: " + fileName + "=> " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public <T> T convertJsonToObj(String jsonData, Class<T> genericClassType) {
        //Gson gson = new Gson();
        LogMe("JsonProcessing", " convertJsonToObj: ");
        Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        try {
            //convert the json string back to object
            T obj = gson.fromJson(jsonData, genericClassType);
            return obj;
        } catch (Exception e) {
            LogMe("JsonProcessing", "JSON data issues : " + jsonData + "=> " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /* Convert obj to json and write to file */
    public <T> String readObjToJson(T obj, String fileName) {
        Gson gson = new Gson();
        String json = gson.toJson(obj);

        try {
            //write converted json data to a file named "file.json"
            FileWriter writer = new FileWriter(fileName);
            writer.write(json);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(json);
        return json;
    }

    public String readFile(Context context, String fileName, Boolean bExternalStorage)
            throws IOException {
        BufferedReader reader;
        File file;
        FileInputStream streamIn;
        InputStreamReader isr;
        int fileSize=0;
        byte[] data1;

        if (bExternalStorage) {
            File sdCard = Environment.getExternalStorageDirectory();
            File directory = new File(sdCard.getAbsolutePath()
                    + "/Download/MyProj/json_data");
            file = new File(directory, fileName);
            fileSize = (int) file.length();

            data1 = new byte[fileSize];
            streamIn = new FileInputStream(file);
            isr = new InputStreamReader(streamIn);
            streamIn.read(data1);
        } else {
            fileName = "json_data/" + fileName;
            InputStream is = context.getAssets().open(fileName);
            isr = new InputStreamReader(is, "UTF-8");
            fileSize = is.available();
            data1 = new byte[fileSize];
            is.read(data1);
        }
        reader = new BufferedReader(isr);

        if(false) {
            String line;
            String data = "";

            while ((line = reader.readLine()) != null) {
                data = data.concat(line);
            }
            jsonData = data;
        }
        else
            jsonData = new String(data1);

        return jsonData;
    }

    /*
    The sPath variable contains the folder structure in the assets folder of the project.
    For example empinfo or json_data or empinfo/retired or empinfo/active
     */
    public String readFileFromPath(Context context, String fileName, String sPath)
            throws IOException {
        BufferedReader reader;
        File file;
        FileInputStream streamIn;
        InputStreamReader isr;
        int fileSize=0;
        byte[] data1;
        Boolean bExternalStorage = false;   //TODO : Setting this to false for now. Future enhancement

        if (bExternalStorage) {
            File sdCard = Environment.getExternalStorageDirectory();
            File directory = new File(sdCard.getAbsolutePath() + sPath +"/");
            file = new File(directory, fileName);
            fileSize = (int) file.length();

            data1 = new byte[fileSize];
            streamIn = new FileInputStream(file);
            isr = new InputStreamReader(streamIn);
            streamIn.read(data1);
        } else {
            fileName = sPath + "/" + fileName;
            InputStream is = context.getAssets().open(fileName);
            isr = new InputStreamReader(is, "UTF-8");
            fileSize = is.available();
            data1 = new byte[fileSize];
            is.read(data1);
        }
        reader = new BufferedReader(isr);

        if(false) {
            String line;
            String data = "";

            while ((line = reader.readLine()) != null) {
                data = data.concat(line);
            }
            jsonData = data;
        }
        else
            jsonData = new String(data1);

        return jsonData;
    }
    /*
    ** ========================
    ** Employee specific
    ** ========================
    */

    ArrayList<Employee> readJson(String data) {
        ArrayList<Employee> arrEmp = new ArrayList<Employee>();
        try {
            JSONObject jObj = new JSONObject(data);
            JSONArray jArray = jObj.getJSONArray("employees");
            LogMe("JSON Processing ", "Total employees : " + jArray.length());

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jEmpObj = jArray.getJSONObject(i);
                Employee e = new Employee();
                e.empid = readJSONString(jEmpObj, "empid");
                e.name = readJSONString(jEmpObj, "name");
                e.surname = readJSONString(jEmpObj, "surname");
                e.designation = readJSONString(jEmpObj, "designation");
                e.department = readJSONString(jEmpObj, "department");
                LogMe("JSON Processing ", "DepartmentTab " + e.department);
                arrEmp.add(e);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrEmp;
    }

    String readJSONString(JSONObject jObj, String key) {
        String result = "";
        try {
            result = jObj.getString(key);

        } catch (JSONException e) {
            LogMe("JsonProcessing", "Ignore " + key);
        }
        return result;
    }

    public ArrayList<Employee> JsonReader() throws IOException {
        if (jsonData == null) {
            LogMe("JsonReader", "Invalid input data");
            return null;
        }
        reader = new JsonReader(new StringReader(jsonData));
        ArrayList<Employee> allEmployees = new ArrayList<>();

        nextObject();
        while (reader.hasNext()) {
            String name = readKey();
            if (name.equalsIgnoreCase("employees")) {
                reader.beginArray();
                while (reader.hasNext()) {
                    nextObject();
                    Employee e = new Employee();
                    e.weekatnd = new Week();
                    e.monthatnd = new Month();

                    while (reader.hasNext()) {
                        String keyname = readKey();

                        if (keyname.equalsIgnoreCase("attendance")) {
                            nextObject();
                            while (reader.hasNext()) {
                                String subkeyname = readKey();
                                if (subkeyname.equalsIgnoreCase("weekly")) {
                                    nextObject();
                                    while (reader.hasNext()) {
                                        Week wk = new Week();
                                        subkeyname = readKey();
                                        if (subkeyname
                                                .equalsIgnoreCase("weekno")) {
                                            wk.weekno = Integer
                                                    .parseInt(readValue());
                                        } else if (subkeyname
                                                .equalsIgnoreCase("weekdays")) {
                                            reader.beginArray();
                                            wk.days = new ArrayList<>();
                                            while (reader.hasNext()) {
                                                nextObject();
                                                Attendance a = new Attendance();
                                                while (reader.hasNext()) {
                                                    subkeyname = readKey();
                                                    String val = readValue();
                                                    if (subkeyname.equalsIgnoreCase("intime"))
                                                        a.intime = java.sql.Timestamp.valueOf("2014-01-01 " + val + ".000");
                                                    else if (subkeyname.equalsIgnoreCase("outtime"))
                                                        a.outtime = java.sql.Timestamp.valueOf("2014-01-01 " + val + ".000");
                                                    else if (subkeyname.equalsIgnoreCase("date"))
                                                        LogMe("Weeks", "java.sql.Date.valueOf(" + val);
                                                    else if (subkeyname.equalsIgnoreCase("day"))
                                                        a.day = val;
                                                       // a.day = Integer.parseInt(val);

                                                    else if (subkeyname.equalsIgnoreCase("hours"))
                                                        a.hours = Float.parseFloat(val);
                                                    else
                                                        LogMe("Weeks", "Unknown");
                                                }
                                                wk.days.add(a);
                                                endObject();
                                            }
                                            reader.endArray();
                                            e.weekatnd = wk;
                                        } //else-if
                                    } //while
                                    endObject();
                                }//if
                                if (subkeyname.equalsIgnoreCase("monthly")) {
                                    LogMe("JsonProcessing",
                                            "monthly attendance found");
                                }
                                if (subkeyname.equalsIgnoreCase("daily")) {
                                    LogMe("JsonProcessing",
                                            "daily attendance found");
                                }
                            }
                            endObject();

                        } else if (keyname.equalsIgnoreCase("empid")) {
                            e.empid = readValue();
                        } else if (keyname.equalsIgnoreCase("name")) {
                            e.name = readValue();
                        } else if (keyname.equalsIgnoreCase("designation")) {
                            e.designation = reader.nextString();
                        } else if (keyname.equalsIgnoreCase("department")) {
                            e.department = readValue();
                        } else
                            readValue();
                    }//while
                    endObject();
                    allEmployees.add(e);
                }
                reader.endArray();
            } else {
                LogMe("JsonReader", "invalid json data");
            }
        }
        return allEmployees;
    }

    String readKey() {
        String key = "";
        try {
            key = reader.nextName();
            LogMe("JsonProcessing:readValue", "Key : " + key);
            return key;

        } catch (IOException e) {
            LogMe("JsonProcessing:readValue", "No key found");
        }
        return key;
    }

    String readValue() {
        String value = "";
        try {
            value = reader.nextString();
            LogMe("JsonProcessing:readValue", "Value : " + value);
            return value;

        } catch (IOException e) {
            LogMe("JsonProcessing:readValue", "No value defined");
        }
        return value;
    }

    void nextObject() throws IOException {
        reader.beginObject();
    }

    void endObject() throws IOException {
        reader.endObject();
    }
}
