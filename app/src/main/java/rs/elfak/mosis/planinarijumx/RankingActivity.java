package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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


public class RankingActivity extends Activity
{
    private static final String request = "3\n";
    private ArrayList<OsobaReducedPlus> usersList;
    private ArrayAdapter<String> usersAdapter;
    private String users;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    InetAddress adr = InetAddress.getByName(Constants.address);
                    Socket socket = new Socket(adr, Constants.PORT);
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
                    printWriter.write(request);
                    printWriter.flush();

                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    users = in.readLine();
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    usersList = gson.fromJson(users, new TypeToken<ArrayList<OsobaReducedPlus>>() {}.getType());

                    printWriter.close();
                    socket.close();

                    /*runOnUiThread(new Runnable() {
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
