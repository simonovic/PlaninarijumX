package rs.elfak.mosis.planinarijumx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by filip on 7/4/15.
 */
public class OsobaMesto
{
    private int id;
    private double lat;
    private double lon;

    public OsobaMesto(int id, double lat, double lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    private OsobaMesto(){}

    @Override
    public String toString()
    {
        String s = null;
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.serializeNulls();

        Gson gson = gsonBuilder.create();
        s = gson.toJson(this);
        return s;
    }

}
