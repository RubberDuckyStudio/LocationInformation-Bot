package won.bot.skeleton;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jose4j.json.internal.json_simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GDS {
    String apiKey="X6RG20EXBIOOERD2YNTZDHK6XTCWNN3L";

    public static void main(String[] args) {
        List<City> test= new ArrayList<>();
        test=getCityByLngLat(-122.084,37.3861);
        System.out.println(test);
    }

    public List<City> getCityByLngLat(double lng, double lat){
        return jsonToCity(getCitiesInJSON(lng,lat));
        //return new ArrayList<>();
    }

    public List<String> getCitiesInJSON(double lng, double lat){
        try {
            URL url = new URL("https://api.geodatasource.com/city?key="+apiKey+"&format=json&lat="+lat+"&lng="+lng);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            List<String> ret= new ArrayList<>();
            while ((output = br.readLine()) != null) {
                ret.add(output);
            }
            conn.disconnect();
            return ret;
        } catch (MalformedURLException e) {

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<City> jsonToCity(List<String> jsonCities){
        List<City> ret= new ArrayList<>();
        for (String str:jsonCities) {
            City tmpCity=new City();
            JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();

            tmpCity.setCountry(jsonObject.get("country").toString());
            tmpCity.setName(jsonObject.get("city").toString());
            tmpCity.setRegion(jsonObject.get("region").toString());
            tmpCity.setLatitude(jsonObject.get("latitude").getAsDouble());
            tmpCity.setLongitude(jsonObject.get("longitude").getAsDouble());

            ret.add(tmpCity);
        }
        return ret;
    }
}
