package rs.elfak.mosis.planinarijumx;

import java.util.ArrayList;

/**
 * Created by filip on 6/23/15.
 */
public class QuestSolver
{
    ArrayList<Place> quest;
    int position;

    public QuestSolver(ArrayList<Place> quest) {
        this.quest = quest;
        position = quest.size() - 1;
    }

    public boolean Solve(String odg)
    {
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
}
