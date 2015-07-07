package rs.elfak.mosis.planinarijumx;

/**
 * Created by filip on 6/23/15.
 */
public class Place {
    public static int ID = 1;
    private int id;
    private String pitanje;
    private String odgovor;
    private double lat;
    private double lng;
    private boolean reseno;


    public Place(double lat, double lng, String odgovor, String pitanje,int pitanjeID)
    {
        this.lat = lat;
        this.lng = lng;
        this.odgovor = odgovor;
        this.pitanje = pitanje;
        this.id = pitanjeID;
        reseno = false;
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

    public boolean isReseno() {
        return reseno;
    }

    public void setReseno(boolean reseno) {
        this.reseno = reseno;
    }
}
