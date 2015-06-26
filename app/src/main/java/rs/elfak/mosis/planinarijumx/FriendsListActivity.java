package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
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
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;


public class FriendsListActivity extends Activity
{
    private ArrayList<OsobaReduced> friends;
    private ArrayAdapter<String> friendsAdapter;
    int userID;
    SharedPreferences shPref;
    private String request = "4\n";
    String prijatelji;
    String friendsID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        shPref = getSharedPreferences(Constants.loginpref, Context.MODE_PRIVATE);
        userID = shPref.getInt(Constants.userIDpref, 0);
        request += userID+"\n";

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
                    prijatelji = in.readLine();
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    friends = gson.fromJson(prijatelji, new TypeToken<ArrayList<OsobaReduced>>(){}.getType());

                    printWriter.close();
                    socket.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            friendsAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1);
                            for (Iterator<OsobaReduced> i = friends.iterator(); i.hasNext(); ) {
                                OsobaReduced os = i.next();
                                friendsAdapter.add(os.getUser());
                                friendsID += os.getId()+" ";
                            }
                            ListView friendsList = (ListView) findViewById(R.id.listFriends);
                            friendsList.setAdapter(friendsAdapter);
                            friendsList.setOnItemClickListener(friendClickListener);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private AdapterView.OnItemClickListener friendClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Toast.makeText(getApplicationContext(), "Klik na prijatelja!", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_friends_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.friend)
        {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null)
                Toast.makeText(getApplicationContext(), "Uređaj ne podržava Bluetooth!", Toast.LENGTH_LONG).show();
            else
            {
                Intent in = new Intent(this, BluetoothActivity.class);
                in.putExtra("friendsID", friendsID);
                startActivity(in);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
