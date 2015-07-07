package rs.elfak.mosis.planinarijumx;

import java.util.ArrayList;

/**
 * Created by filip on 6/23/15.
 */
public class QuestSolver
{
    ArrayList<Place> quest;
    int position;
    int questID;
    boolean zapocet;
    private int poeni;


    public QuestSolver(ArrayList<Place> quest,int id) {
        this.quest = quest;
        position = quest.size() - 1;
        questID = id;
        zapocet = false;
        poeni = 0;
    }

    public boolean Solve(String odg)
    {
        if(odg == null)
            return false;
        if(odg.equals(""))
            return false;
        System.out.println("uneo si"+odg);

        for(int i = 0; i < quest.size(); i++)
            if(position == quest.get(i).getId())
            {
                System.out.println(quest.get(i).getOdgovor());
                if(odg.equals(quest.get(i).getOdgovor()))
                {
                    position--;
                    return true;
                }
                else
                    return false;
            }
        return false;
    }

    public Place getPitanje()
    {
        for(int i = 0; i < quest.size(); i++)
            if(position == quest.get(i).getId())
                return quest.get(i);

        return null;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        if(position == quest.size() + 1)
            zapocet = false;
        else
            zapocet = true;
    }

    public ArrayList<Place> getQuest() {
        return quest;
    }

    public boolean isZapocet() {
        return zapocet;
    }

    public void setZapocet(boolean zapocet) {
        this.zapocet = zapocet;
    }

    public int getPoeni() {
        return poeni;
    }

    public void setPoeni(int poeni) {
        this.poeni = poeni;
    }

    public void addPoeni(int amount)
    {
        poeni += amount;
    }

    public int getQuestID() {
        return questID;
    }

    public void setQuestID(int questID) {
        this.questID = questID;
    }
}
