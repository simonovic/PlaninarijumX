package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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


public class RankingActivity extends Activity
{
    private static final String request = "3\n";
    private ArrayList<OsobaReducedPlus> usersList;
    private ArrayAdapter<String> usersAdapter;
    private String users;
    EditText rangEditText;
    private int userID;
    SharedPreferences shPref;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        rangEditText = (EditText)findViewById(R.id.currang);
        shPref = getSharedPreferences(Constants.loginpref, Context.MODE_PRIVATE);
        userID = shPref.getInt(Constants.userIDpref, 0);

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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int pom;
                            int rang = 0;
                            do
                            {
                                pom = usersList.get(rang).getId();
                                rang++;
                            } while (pom != userID);
                            rangEditText.setText(""+rang);
                            usersAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1);
                            for (Iterator<OsobaReducedPlus> i = usersList.iterator(); i.hasNext(); ) {
                                OsobaReducedPlus o = i.next();
                                usersAdapter.add(o.getUser());
                            }
                            ListView plListView = (ListView) findViewById(R.id.ranking);
                            plListView.setAdapter(usersAdapter);
                            plListView.setOnItemClickListener(osobaClickListener);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private AdapterView.OnItemClickListener osobaClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Gson gson = new GsonBuilder().serializeNulls().create();
            OsobaReducedPlus q = usersList.get(position);
            String plString = gson.toJson(q);
            Intent i = new Intent(RankingActivity.this, PlaninaActivity.class);
            i.putExtra("quest", plString);
            //startActivity(i);
            Toast.makeText(getApplicationContext(), "Klik na korisnika!", Toast.LENGTH_LONG).show();
        }
    };
}
