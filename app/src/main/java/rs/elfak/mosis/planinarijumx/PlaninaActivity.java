package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import java.util.Map;


public class PlaninaActivity extends Activity
{
    private static Planina pl;
    private ArrayList<Quest> questList;
    private ArrayAdapter<String> questAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planina);

        Bundle extras = getIntent().getExtras();
        String plString = extras.getString("planina");
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
                            setTitle(pl.getIme());
                            questAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1);
                            int br = 1;
                            for (Iterator<Quest> i = questList.iterator(); i.hasNext(); ) {
                                Quest q = i.next();
                                questAdapter.add(br+".  "+q.getIme());
                                br++;
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
    protected void onResume() {
        super.onResume();
        LogActivity.trenutnaAktivnost = this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_dodaj_kviz_na_planini)
        {
            Intent i = new Intent(this, MapActivity.class);
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
            final Quest q = questList.get(position);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String sendBuff = "11\n" + LogActivity.userID + "\n" + q.getId() + "\n";
                        InetAddress adr = InetAddress.getByName(Constants.address);
                        Socket socket = new Socket(adr, Constants.PORT);
                        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                        printWriter.write(sendBuff);
                        printWriter.flush();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        final String info = in.readLine();
                        final int pozicija = Integer.parseInt(in.readLine());
                        in.close();
                        printWriter.close();
                        socket.close();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(getApplication(), MapActivity.class);
                                String podaci = q.getIme() + "\n" + q.getId() + "\n" + pozicija + "\n" + info;
                                i.putExtra("questInfo",podaci);
                                startActivity(i);
                            }
                        });

                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    };
}
