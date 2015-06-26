package rs.elfak.mosis.planinarijumx;

import com.google.gson.*;

/**
 * Created by Stefan Simonovic on 26/06/2015.
 */

public class OsobaPlus
{
    private int id;
    private String user;
    private String pass;
    private String ime;
    private String prezime;
    private String slika;
    private String brTelefona;
    private int brPoena;
    private int rank;

    private OsobaPlus() {}

    public OsobaPlus(int id, String user, String pass, String ime, String prezime, String slika, String brTelefona, int brPoena, int rank)
    {
        this.id = id;
        this.user = user;
        this.pass = pass;
        this.ime = ime;
        this.prezime = prezime;
        this.slika = slika;
        this.brTelefona = brTelefona;
        this.brPoena = brPoena;
        this.rank = rank;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getPass()
    {
        return pass;
    }

    public void setPass(String pass)
    {
        this.pass = pass;
    }

    public String getSlika()
    {
        return slika;
    }

    public void setSlika(String slika)
    {
        this.slika = slika;
    }

    public String getBrTelefona()
    {
        return brTelefona;
    }

    public void setBrTelefona(String brTelefona)
    {
        this.brTelefona = brTelefona;
    }

    public int getBrPoena()
    {
        return brPoena;
    }

    public void setBrPoena(int brPoena)
    {
        this.brPoena = brPoena;
    }

    public String getIme()
    {
        return ime;
    }

    public void setIme(String ime)
    {
        this.ime = ime;
    }

    public String getPrezime()
    {
        return prezime;
    }

    public void setPrezime(String prezime)
    {
        this.prezime = prezime;
    }

    public int getRank()
    {
        return rank;
    }

    public void setRank(int rank)
    {
        this.rank = rank;
    }

    @Override
    public String toString()
    {
        Gson gson = new GsonBuilder().serializeNulls().create();

        return gson.toJson(this);
    }
}
