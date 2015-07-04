package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class QuestsActivity extends Activity
{
    ListView questList;
    QuestListAdapter adapter;

    String[] name ={
            "Safari",
            "Camera",
            "Global",
            "FireFox",
            "UC Browser",
            "Android Folder",
            "VLC Player",
            "Cold War"
    };

    String[] status ={
            "Dovrseno",
            "Nedovrseno",
            "Dovrseno",
            "Nedovrseno",
            "Nedovrseno",
            "Dovrseno",
            "Dovrseno",
            "Nedovrseno"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quests);
        try {

            adapter = new QuestListAdapter(this, name, status);
            questList = (ListView) findViewById(R.id.list);
            questList.setAdapter(adapter);

            questList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getApplicationContext(), name[position], Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
