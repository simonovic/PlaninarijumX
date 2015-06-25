package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.w3c.dom.Text;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends Activity
{
    int userID;
    SharedPreferences shPref;
    private static final String request = "5\nplanine\n";
    static LatLng myLocation = new LatLng(43.319425, 21.899487);
    private ArrayList<Planina> planine;
    private ArrayAdapter<String> planineAdapter;
    public static LatLng MyLocation =  new LatLng(43.319425, 21.899487);
    LocationManager locationManager;
    LocationListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shPref = getSharedPreferences(Constants.loginpref, Context.MODE_PRIVATE);

        //Bundle extras = getIntent().getExtras();
        //userID = extras.getInt("userID");

        /*handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"Posalji serveru lokaciju", Toast.LENGTH_SHORT).show();
                handler.postDelayed(runnable, 13000);
            }
        };
        handler.postDelayed(runnable, 13000);*/;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    /*InetAddress adr = InetAddress.getByName(Constants.address);
                    Socket socket = new Socket(adr, Constants.PORT);
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
                    printWriter.write(request);
                    printWriter.flush();

                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String pl = in.readLine();
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    planine = gson.fromJson(pl, new TypeToken<ArrayList<Planina>>(){}.getType());

                    String pom = planine.get(0).toString();

                    printWriter.close();
                    socket.close();*/

                    Planina p1 = new Planina(1,"Ime1", 2000, 43.433, 43.433);
                    Planina p2 = new Planina(2,"Ime2", 3000, 53.433, 53.433);
                    Planina p3 = new Planina(3,"Ime3", 4000, 63.433, 63.433);

                    planine = new ArrayList<Planina>();
                    planine.add(p1);
                    planine.add(p2);
                    planine.add(p3);
                    planine.add(p1);
                    planine.add(p2);
                    planine.add(p3);
                    planine.add(p1);
                    planine.add(p2);
                    planine.add(p3);
                    planine.add(p1);
                    planine.add(p2);
                    planine.add(p3);
                    planine.add(p1);
                    planine.add(p2);

                    PopuniPlanineList(planine);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLocation = new LatLng(location.getLatitude(),location.getLongitude());
                Toast.makeText(getApplicationContext(),"Posalji serveru lokaciju", Toast.LENGTH_SHORT).show();

                //TODO: Posalji serveru lokaciju

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, listener);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(listener);
        locationManager = null;
        listener = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.mapa:
                Intent i = new Intent(MainActivity.this,MapActivity.class);
                startActivity(i);
                break;
            case R.id.bluetooth:
                Intent in = new Intent(this, FriendsListActivity.class);
                startActivity(in);
                break;
            case R.id.profil:
                Intent inn = new Intent(this, MyProfileActivity.class);
                startActivity(inn);
                break;
            case R.id.logout:
                SharedPreferences.Editor editor = shPref.edit();
                editor.putInt(Constants.userIDpref, 0);
                editor.commit();
                Intent ii = new Intent(this, LogActivity.class);
                startActivity(ii);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private AdapterView.OnItemClickListener planinaClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Toast.makeText(getApplicationContext(), "Klik na planinu!", Toast.LENGTH_LONG).show();
        }
    };

    private void PopuniPlanineList(ArrayList<Planina> planine)
    {
        planineAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1);
        for (Iterator<Planina> i = planine.iterator(); i.hasNext();)
        {
            Planina pl = i.next();
            planineAdapter.add(pl.getIme());
            Log.v("PlaninarijumX", pl.getIme());
        }
        ListView plListView = (ListView)findViewById(R.id.planinaListView);
        plListView.setAdapter(planineAdapter);
        plListView.setOnItemClickListener(planinaClickListener);
    }
}
