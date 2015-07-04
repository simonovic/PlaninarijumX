package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;


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

        adapter = new QuestListAdapter(this, name, status);
        questList = (ListView) findViewById(R.id.list);
        questList.setAdapter(adapter);

        questList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), name[position], Toast.LENGTH_LONG).show();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    InetAddress adr = InetAddress.getByName(Constants.address);
                    Socket socket = new Socket(adr, Constants.PORT);
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
                    /*printWriter.write(request);
                    printWriter.flush();

                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    pl = in.readLine();
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    planine = gson.fromJson(pl, new TypeToken<ArrayList<Planina>>() {}.getType());

                    printWriter.close();
                    socket.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            planineAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1);
                            for (Iterator<Planina> i = planine.iterator(); i.hasNext(); ) {
                                Planina pl = i.next();
                                planineAdapter.add(pl.getIme());
                            }
                            ListView plListView = (ListView) findViewById(R.id.planinaListView);
                            plListView.setAdapter(planineAdapter);
                            plListView.setOnItemClickListener(planinaClickListener);
                        }
                    });*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
