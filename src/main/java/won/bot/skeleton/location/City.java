package won.bot.skeleton.location;

import java.util.ArrayList;
import java.util.List;

public class City {
    private double longitude;
    private double latitude;
    private String name;
    private String country;
    private String region;
    private List<InterestingLocation> interestingLocations;

    public City(){
        interestingLocations = new ArrayList<>();
    }

    public List<InterestingLocation> getInterestingLocations() {
        return interestingLocations;
    }

    public void setInterestingLocations(List<InterestingLocation> interestingLocations) {
        this.interestingLocations = interestingLocations;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "City{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", region='" + region + '\'' +
                ", interestingLocations=" + interestingLocations.toString() +
                '}';
    }
}
