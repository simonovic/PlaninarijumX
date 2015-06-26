package rs.elfak.mosis.planinarijumx;

import com.google.gson.*;

/**
 * Created by filip on 6/24/15.
 */
public class NovaOsoba
{;
    private String user;
    private String pass;
    private String ime;
    private String prezime;
    private String slika;
    private String brTelefona;
    private int velicinaSlike;

    private NovaOsoba()
    {

    }

    public NovaOsoba(String brTelefona, String ime, String pass, String prezime, String slika, String user, int vS) {
        this.brTelefona = brTelefona;
        this.ime = ime;
        this.pass = pass;
        this.prezime = prezime;
        this.slika = slika;
        this.user = user;
        this.velicinaSlike = vS;
    }

    public String getBrTelefona() {
        return brTelefona;
    }

    public String getIme() {
        return ime;
    }

    public String getPass() {
        return pass;
    }

    public String getPrezime() {
        return prezime;
    }

    public String getSlika() {
        return slika;
    }

    public String getUser() {
        return user;
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


}
