package rs.elfak.mosis.planinarijumx;

/**
 * Created by Stefan Simonovic on 07/07/2015.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OsobaRadiQuest
{
    private int id;
    private int osobaId;
    private int questId;
    private int mestoBr;

    private OsobaRadiQuest() {}

    public OsobaRadiQuest(int id, int osobaId, int questId, int mestoBr)
    {
        this.id = id;
        this.osobaId = osobaId;
        this.questId = questId;
        this.mestoBr = mestoBr;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getOsobaId()
    {
        return osobaId;
    }

    public void setOsobaId(int osobaId)
    {
        this.osobaId = osobaId;
    }

    public int getQuestId()
    {
        return questId;
    }

    public void setQuestId(int questId)
    {
        this.questId = questId;
    }

    public int getMestoBr()
    {
        return mestoBr;
    }

    public void setMestoBr(int mestoBr)
    {
        this.mestoBr = mestoBr;
    }

    @Override
    public String toString()
    {
        Gson gson = new GsonBuilder().serializeNulls().create();

        return gson.toJson(this);
    }

}
