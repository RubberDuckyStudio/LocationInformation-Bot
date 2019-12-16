package won.bot.skeleton.location;

import com.github.jsonldjava.utils.Obj;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class GDS {
    String apiKey = "X6RG20EXBIOOERD2YNTZDHK6XTCWNN3L";

    public List<City> getCityByLngLat(double lng, double lat) {
        return jsonToCity(getCitiesInJSON(lng, lat));
        //return new ArrayList<>();
    }

    public List<String> getCitiesInJSON(double lng, double lat) {
        try {
            URL url = new URL("https://api.geodatasource.com/city?key=" + apiKey + "&format=json&lat=" + lat + "&lng=" + lng);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            List<String> ret = new ArrayList<>();
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

    public List<City> jsonToCity(List<String> jsonCities) {
        List<City> ret = new ArrayList<>();
        for (String str : jsonCities) {
            City tmpCity = new City();
            JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();

            tmpCity.setCountry(jsonObject.get("country").toString());
            tmpCity.setName(jsonObject.get("city").toString());
            tmpCity.setRegion(jsonObject.get("region").toString());
            tmpCity.setLatitude(jsonObject.get("latitude").getAsDouble());
            tmpCity.setLongitude(jsonObject.get("longitude").getAsDouble());

            additionalInfoToCity(tmpCity,tmpCity.getRegion());

            ret.add(tmpCity);
        }
        return ret;
    }

    public void additionalInfoToCity(City tmpCity, String name){
        String wiki_api_eng="https://en.wikipedia.org/w/api.php?";
        String wiki_api_de="https://de.wikipedia.org/w/api.php?";
        int searchRadius = 5000; //meter, max is 10000 defined by API
        String urlString = wiki_api_eng + "action=query&list=geosearch&gsradius="+searchRadius+"&gscoord=" + tmpCity.getLatitude() + "|" + tmpCity.getLongitude() + "&format=json";
        System.out.println(urlString);
        String wikiStr = readFromUrl(urlString);

        JsonObject jsonObject = new JsonParser().parse(wikiStr).getAsJsonObject();

        String query = jsonObject.get("query").toString();

        String[] queryArray = query.substring(14,query.length()- 1).split(",");
        List<InterestingLocation> intLocList = new ArrayList<>();
        InterestingLocation tempIntLoc = new InterestingLocation();
        int jsonIndex = 1;
        for (String str: queryArray) {
            if (str.contains("pageid")){
                String curr = str.substring(10);
                tempIntLoc.setWikiPageId(curr);

                String currentWikiStr = readFromUrl(wiki_api_eng + "action=query&prop=info&pageids="+curr+"&inprop=url&format=json");
                JsonObject currentWikiJsonObejct = new JsonParser().parse(currentWikiStr).getAsJsonObject();
                JsonObject r1 = currentWikiJsonObejct.get("query").getAsJsonObject();
                JsonObject r2 = r1.get("pages").getAsJsonObject();
                JsonObject r3 = r2.get(curr).getAsJsonObject();
                String result = r3.get("fullurl").toString();
                tempIntLoc.setFullUrl(result.substring(1,result.length()-1));
            }else if (str.contains("title")){
                tempIntLoc.setTitle(str.substring(9,str.length()-1));
            }else if (str.contains("dist")){
                tempIntLoc.setDistance(Float.parseFloat(str.substring(7)));
            }
            if (jsonIndex%8==0){
                intLocList.add(tempIntLoc);
                tempIntLoc = new InterestingLocation();
            }
            jsonIndex++;
        }
        //System.out.println(intLocList.toString());

        tmpCity.setInterestingLocations(intLocList);
    }

    private static String readFromUrl(String urlString){
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null){
                result.append(line);
            }
            rd.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return result.toString() + "\n";
    }

}
