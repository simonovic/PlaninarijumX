package rs.elfak.mosis.planinarijumx;

/**
 * Created by Stefan Simonovic on 04/07/2015.
 */

public class Quest
{
    private int id;
    private String ime;
    private int brMesta;
    private int planina_id;
    private int osoba_id;

    private Quest() {}

    public Quest(int id, String ime, int brMesta, int planina_id, int osoba_id)
    {
        this.id = id;
        this.ime = ime;
        this.brMesta = brMesta;
        this.planina_id = planina_id;
        this.osoba_id = osoba_id;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getBrMesta()
    {
        return brMesta;
    }

    public void setBrMesta(int brMesta)
    {
        this.brMesta = brMesta;
    }

    public int getPlanina_id()
    {
        return planina_id;
    }

    public void setPlanina_id(int planina_id)
    {
        this.planina_id = planina_id;
    }

    public int getOsoba_id()
    {
        return osoba_id;
    }

    public void setOsoba_id(int osoba_id)
    {
        this.osoba_id = osoba_id;
    }

    public String getIme()
    {
        return ime;
    }

    public void setIme(String ime)
    {
        this.ime = ime;
    }

    @Override
    public String toString()
    {
        return "Quest [id=" + id + ", ime=" + ime + ", brMesta=" + brMesta
                + ", planina_id=" + planina_id + ", osoba_id=" + osoba_id + "]";
    }


}
