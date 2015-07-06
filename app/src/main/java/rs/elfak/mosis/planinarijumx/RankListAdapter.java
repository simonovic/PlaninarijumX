package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Stefan Simonovic on 06/07/2015.
 */
public class RankListAdapter extends ArrayAdapter<String>
{
    private final Context context;
    private final String[] userRank;
    private final String[] userName;
    private final String[] userPoints;

    public RankListAdapter(Activity context, String[] userRank, String[] userName, String[] userPoints)
    {
        super(context, R.layout.list_rank_item, userRank);

        this.context = context;
        this.userRank = userRank;
        this.userName = userName;
        this.userPoints = userPoints;
    }

    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.list_rank_item, null, true);

        TextView rankText = (TextView) rowView.findViewById(R.id.userRank);
        TextView nameText = (TextView) rowView.findViewById(R.id.userName);
        TextView pointsText = (TextView) rowView.findViewById(R.id.userPoints);
        rankText.setText(userRank[position]);
        nameText.setText(userName[position]);
        pointsText.setText(userPoints[position]);

        return rowView;
    };
}
