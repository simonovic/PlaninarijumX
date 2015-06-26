package rs.elfak.mosis.planinarijumx;

/**
 * Created by Stefan Simonovic on 26/06/2015.
 */

public class OsobaReduced
{
    private int id;
    private String user;

    private OsobaReduced() {}

    public OsobaReduced(int id, String user)
    {
        this.id = id;
        this.user = user;
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

    @Override
    public String toString()
    {
        return "Osoba [id=" + id + ", user=" + user + "]";
    }
}
