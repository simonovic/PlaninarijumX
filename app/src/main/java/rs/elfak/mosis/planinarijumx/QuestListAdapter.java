package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Stefan Simonovic on 26/06/2015.
 */
public class QuestListAdapter extends ArrayAdapter<String>
{
    private final Context context;
    private final String[] name;
    private final String[] status;

    public QuestListAdapter(Activity context, String[] name, String[] status)
    {
        super(context, R.layout.list_quest_item, name);

        this.context = context;
        this.name = name;
        this.status = status;
    }

    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.list_quest_item, null, true);

        TextView nameText = (TextView) rowView.findViewById(R.id.questName);
        TextView statusText = (TextView) rowView.findViewById(R.id.questStatus);
        nameText.setText(name[position]);
        statusText.setText(status[position]);

        return rowView;
    };
}
