package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;


public class PlaninaActivity extends Activity
{
    private String plString;
    private Planina pl;
    private ArrayList<Quest> questList;
    private ArrayAdapter<String> questAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planina);

        Bundle extras = getIntent().getExtras();
        plString = extras.getString("planina");
        Gson gson = new GsonBuilder().serializeNulls().create();
        pl = gson.fromJson(plString, Planina.class);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                        String request = "9\n"+pl.getId()+"\n";
                        InetAddress adr = InetAddress.getByName(Constants.address);
                        Socket socket = new Socket(adr, Constants.PORT);
                        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
                        printWriter.write(request);
                        printWriter.flush();

                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String serverResponse = in.readLine();
                        Gson gson = new GsonBuilder().serializeNulls().create();
                        questList = gson.fromJson(serverResponse, new TypeToken<ArrayList<Quest>>() {}.getType());

                        printWriter.close();
                        socket.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            questAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1);
                            for (Iterator<Quest> i = questList.iterator(); i.hasNext(); ) {
                                Quest q = i.next();
                                questAdapter.add(q.getIme());
                            }
                            ListView plListView = (ListView) findViewById(R.id.questListView);
                            plListView.setAdapter(questAdapter);
                            plListView.setOnItemClickListener(questClickListener);
                        }
                    });

                }
                catch (IOException e) { e.printStackTrace(); }
                }
            }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_planina, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_dodaj_kviz_na_planini)
        {
            Intent i = new Intent(this,MapActivity.class);
            i.putExtra("planinaID",pl.getId());
            startActivity(i);
        }
        else if (id == R.id.mapa)
        {

        }
        return super.onOptionsItemSelected(item);
    }

    private AdapterView.OnItemClickListener questClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Gson gson = new GsonBuilder().serializeNulls().create();
            Quest q = questList.get(position);
            String plString = gson.toJson(q);
            Intent i = new Intent(PlaninaActivity.this, PlaninaActivity.class);
            i.putExtra("quest", plString);
            startActivity(i);
        }
    };
}
