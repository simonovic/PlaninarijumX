package rs.elfak.mosis.planinarijumx;

/**
 * Created by Stefan Simonovic on 24/06/2015.
 */
public class Planina
{
    private int id;
    private String ime;
    private int visina;
    private double lat;
    private double lon;

    private Planina() {}

    public Planina(int id, String ime, int visina, double lat, double lon)
    {
        this.id = id;
        this.ime = ime;
        this.visina = visina;
        this.lat = lat;
        this.lon = lon;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getIme()
    {
        return ime;
    }

    public void setIme(String ime)
    {
        this.ime = ime;
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

    @Override
    public String toString()
    {
        return "Planina [id=" + id + ", ime=" + ime + ", visina=" + visina + ", lat=" + lat + ", lon=" + lon + "]";
    }
}
