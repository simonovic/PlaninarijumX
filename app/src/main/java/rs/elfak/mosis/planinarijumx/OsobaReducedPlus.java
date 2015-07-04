package rs.elfak.mosis.planinarijumx;

/**
 * Created by Stefan Simonovic on 04/07/2015.
 */

public class OsobaReducedPlus
{
    private int id;
    private String user;
    protected int brPoena;

    private OsobaReducedPlus() {}

    public OsobaReducedPlus(int id, String user, int brPoena)
    {
        this.id = id;
        this.user = user;
        this.brPoena = brPoena;
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
    public int getBrPoena()
    {
        return brPoena;
    }
    public void setBrPoena(int brPoena)
    {
        this.brPoena = brPoena;
    }
    @Override
    public String toString()
    {
        return "Osoba [id=" + id + ", user=" + user + ", brPoena=" + brPoena + "]";
    }
}