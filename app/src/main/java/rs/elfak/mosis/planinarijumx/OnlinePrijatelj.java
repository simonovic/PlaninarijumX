package rs.elfak.mosis.planinarijumx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by filip on 7/5/15.
 */
public class OnlinePrijatelj
{
    private int id;
    private String user;
    private double lat;
    private double lon;
    private String ip;

    public OnlinePrijatelj(int id, double lat, double lon, String user, String IP) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.user = user;
        this.ip = IP;
    }

    private OnlinePrijatelj(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString()
    {
        String s = null;
        // Gson gson = new Gson().
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.serializeNulls();

        Gson gson = gsonBuilder.create();
        s = gson.toJson(this);
        return s;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
