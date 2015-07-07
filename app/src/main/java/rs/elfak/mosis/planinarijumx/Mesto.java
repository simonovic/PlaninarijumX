package rs.elfak.mosis.planinarijumx;

/**
 * Created by Stefan Simonovic on 07/07/2015.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Mesto
{
    private int id;
    private String pitanje = null;
    private String odgovor = null;
    private int visina = 0;
    private double lat;
    private double lon;
    private int redBroj;
    private int questId;

    private Mesto(){}

    public Mesto(int id, String pitanje, String odgovor, int visina, double lat, double lon, int redBroj, int questId)
    {
        super();
        this.id = id;
        this.pitanje = pitanje;
        this.odgovor = odgovor;
        this.visina = visina;
        this.lat = lat;
        this.lon = lon;
        this.redBroj = redBroj;
        this.questId = questId;
    }

    public Mesto(int id, double lat, double lon, int redBroj, int questId)
    {
        super();
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.redBroj = redBroj;
        this.questId = questId;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getPitanje()
    {
        return pitanje;
    }

    public void setPitanje(String pitanje)
    {
        this.pitanje = pitanje;
    }

    public String getOdgovor()
    {
        return odgovor;
    }

    public void setOdgovor(String odgovor)
    {
        this.odgovor = odgovor;
    }

    public int getVisina()
    {
        return visina;
    }

    public void setVisina(int visina)
    {
        this.visina = visina;
    }

    public double getLat()
    {
        return lat;
    }

    public void setLat(double lat)
    {
        this.lat = lat;
    }

    public double getLon()
    {
        return lon;
    }

    public void setLon(double lon)
    {
        this.lon = lon;
    }

    public int getRedBroj()
    {
        return redBroj;
    }

    public void setRedBroj(int redBroj)
    {
        this.redBroj = redBroj;
    }

    public int getQuestId()
    {
        return questId;
    }

    public void setQuestId(int questId)
    {
        this.questId = questId;
    }

    @Override
    public String toString()
    {
        Gson gson = new GsonBuilder().serializeNulls().create();

        return gson.toJson(this);
    }

}
