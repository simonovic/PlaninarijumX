package rs.elfak.mosis.planinarijumx;

/**
 * Created by filip on 6/23/15.
 */
public class Place {
    public static int ID = 0;
    private int id;
    private String pitanje;
    private String odgovor;
    private double lat;
    private double lng;


    public Place(double lat, double lng, String odgovor, String pitanje)
    {
        id = ID;
        this.lat = lat;
        this.lng = lng;
        this.odgovor = odgovor;
        this.pitanje = pitanje;
        ID++;
    }

    public int getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getOdgovor() {
        return odgovor;
    }

    public String getPitanje() {
        return pitanje;
    }
}
