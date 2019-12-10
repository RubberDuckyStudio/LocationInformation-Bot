package won.bot.skeleton.location;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.jose4j.json.internal.json_simple.JSONObject;
import won.bot.skeleton.location.City;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GDS {
    String apiKey="X6RG20EXBIOOERD2YNTZDHK6XTCWNN3L";

    public List<City> getCityByLngLat(double lng, double lat){
        List<City> tmpList = jsonToCityFromGeoDataSource(getCitiesInJSONFromGeoDataSource(lng,lat));
        for (City tmpCity: tmpList) {
            jsonToCityFromRapidAPI(getMoreInfosFromRapidAPI(tmpCity.getCountry()),tmpCity);
        }
        return tmpList;
    }

    private List<String> getCitiesInJSONFromGeoDataSource(double lng, double lat){
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

    private List<City> jsonToCityFromGeoDataSource(List<String> jsonCities){
        List<City> ret= new ArrayList<>();
        try {
            for (String str:jsonCities) {
                //System.out.println("String: "+str+" anzCities:"+jsonCities.size());

                City tmpCity = new City();
                JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();

                tmpCity.setCountry(jsonObject.get("country").toString());
                tmpCity.setName(jsonObject.get("city").toString());
                tmpCity.setRegion(jsonObject.get("region").toString());
                tmpCity.setLatitude(jsonObject.get("latitude").getAsDouble());
                tmpCity.setLongitude(jsonObject.get("longitude").getAsDouble());

                ret.add(tmpCity);
            }
        }catch(UnsupportedOperationException e){
            System.out.println("No city found");
        }
        return ret;
    }


    private String getMoreInfosFromRapidAPI(String countryCode){
        OkHttpClient client = new OkHttpClient();
        System.out.println("CountryCode: "+countryCode);
        Request request = new Request.Builder()
                .url("https://restcountries-v1.p.rapidapi.com/alpha/"+countryCode.replace("\"",""))
                .get()
                .addHeader("x-rapidapi-host", "restcountries-v1.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "a6e18e2b94msh3b3d516a1ba8628p1e8c03jsn2b1f72c839c0")
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<City> jsonToCityFromRapidAPI(String moreInfos, City city){
        List<City> ret= new ArrayList<>();
        try {

            //System.out.println("String: "+moreInfos);
            JsonObject jsonObject = new JsonParser().parse(moreInfos).getAsJsonObject();

            city.setEnglishName(jsonObject.get("name").toString());
            city.setCapital(jsonObject.get("capital").toString());
            city.setPopulation(jsonObject.get("population").getAsInt());
            city.setArea(jsonObject.get("area").getAsInt());
            //TODO: sehr hässlich ....
            //city.setCallingCodes(Collections.singletonList(Integer.valueOf(jsonObject.getAsJsonObject("callingCodes").get("0").toString())));
            //city.setCallingCodes(Collections.singletonList(Integer.valueOf(jsonObject.getAsJsonObject("topLevelDomain").get("0").toString())));
            //city.setCallingCodes(Collections.singletonList(Integer.valueOf(jsonObject.getAsJsonObject("timezones").get("0").toString())));


        }catch(UnsupportedOperationException e){
            System.out.println("No city found");
        }
        return ret;
    }

}
