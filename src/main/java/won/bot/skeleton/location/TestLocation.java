package won.bot.skeleton.location;

public class TestLocation {
    public static void main(String[] args) {
        GDS testGDS = new GDS();
        System.out.println("List: "+testGDS.getCityByLngLat(42,90));
    }
}
