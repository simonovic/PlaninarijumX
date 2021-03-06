package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;


public class MainActivity extends Activity
{
    SharedPreferences shPref;
    private static final String request = "5\n";
    private ArrayList<Planina> planine = null;
    private ArrayAdapter<String> planineAdapter;
    private String pl;
    public static LatLng MyLocation =  new LatLng(43.319425, 21.899487);
    LocationManager locationManager;
    LocationListener listener;
    private final String STATE_PLANINE = "planine";
    private Thread nitZaPitanja;
    ServerSocket serverSocket;
    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shPref = getSharedPreferences(Constants.loginpref, Context.MODE_PRIVATE);

        if (savedInstanceState == null)
        {

        }
        else
        {
            pl = savedInstanceState.getString(STATE_PLANINE);
            Gson gson = new GsonBuilder().serializeNulls().create();
            planine = new ArrayList<Planina>();
            planine = gson.fromJson(pl, new TypeToken<ArrayList<Planina>>() {}.getType());

            planineAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1);
            for (Iterator<Planina> i = planine.iterator(); i.hasNext(); ) {
                Planina pl = i.next();
                planineAdapter.add(pl.getIme());
            }
            ListView plListView = (ListView) findViewById(R.id.planinaListView);
            plListView.setAdapter(planineAdapter);
            plListView.setOnItemClickListener(planinaClickListener);
        }

        startListening();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putString(STATE_PLANINE, pl);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogActivity.trenutnaAktivnost = this;
    }

    private void startListening()
    {
        nitZaPitanja = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    serverSocket = new ServerSocket(Constants.FRIENDPORT);

                    socket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    final String pitanje = in.readLine();
                    final double lat = Double.parseDouble(in.readLine());
                    final double lon = Double.parseDouble(in.readLine());
                    final String userPrijatelja = in.readLine();

                    if(pitanje != null)
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LogActivity.trenutnaAktivnost);
                                LayoutInflater inflater = LogActivity.trenutnaAktivnost.getLayoutInflater();
                                final View view = inflater.inflate(R.layout.place_layout, null);
                                ((EditText) view.findViewById(R.id.add_question)).setText(pitanje);
                                ((EditText) view.findViewById(R.id.add_question)).postInvalidate();
                                final EditText odgovor = (EditText) view.findViewById(R.id.add_answer);
                                ((EditText) view.findViewById(R.id.add_question)).setKeyListener(null);
                                builder.setView(view);
                                builder.setTitle("Pitao te: " + userPrijatelja);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        vratiOdgovor(odgovor.getText().toString());
                                    }
                                });

                                AlertDialog alertDialog = builder.create();
                                alertDialog.setCanceledOnTouchOutside(true);
                                alertDialog.show();

                            }
                        });
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        nitZaPitanja.start();
    }

    private void vratiOdgovor(final String odgovor)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                    printWriter.write(odgovor);
                    printWriter.flush();
                    printWriter.close();
                    socket.close();
                    serverSocket.close();
                    startListening();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(locationManager == null) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            listener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    MyLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try
                            {
                                if(LogActivity.userID == 0)
                                {

                                    SharedPreferences sharedPreferences= getSharedPreferences(Constants.loginpref, Context.MODE_PRIVATE);
                                    LogActivity.userID = sharedPreferences.getInt(Constants.userIDpref, 0);
                                }
                                OsobaMesto osobaMesto = new OsobaMesto(LogActivity.userID,MyLocation.latitude,MyLocation.longitude);
                                String sendBuff = "10\n" + osobaMesto.toString() + "\n";
                                InetAddress adr = InetAddress.getByName(Constants.address);
                                Socket socket = new Socket(adr, Constants.PORT);
                                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
                                printWriter.write(sendBuff);
                                printWriter.flush();
                                printWriter.close();
                                socket.close();
                                //osobaMesto = null;

                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }

                        }
                    }).start();


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

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Constants.perioda, 0, listener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constants.perioda, 0, listener);
        }

        if((planine == null))
        {
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
                        pl = in.readLine();
                        Gson gson = new GsonBuilder().serializeNulls().create();
                        planine = gson.fromJson(pl, new TypeToken<ArrayList<Planina>>() {}.getType());

                        printWriter.close();
                        socket.close();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int br = 1;
                                planineAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1);
                                for (Iterator<Planina> i = planine.iterator(); i.hasNext(); ) {
                                    Planina pl = i.next();
                                    planineAdapter.add(br+".  "+pl.getIme());
                                    br++;
                                }
                                ListView plListView = (ListView) findViewById(R.id.planinaListView);
                                plListView.setAdapter(planineAdapter);
                                plListView.setOnItemClickListener(planinaClickListener);
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void disconnect()
    {
        LogActivity.userID = 0;
        LogActivity.userName = "false";
        if(nitZaPitanja != null)
        {

            try {
                if(socket != null)
                    if(!socket.isClosed())
                        socket.close();
                if(serverSocket != null)
                    if(!serverSocket.isClosed())
                        serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(nitZaPitanja.isAlive())
                nitZaPitanja.interrupt();

        }
        if(locationManager != null) {
            locationManager.removeUpdates(listener);
            locationManager = null;
            listener = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();

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
               // disconnect();
                SharedPreferences.Editor editor = shPref.edit();
                editor.putInt(Constants.userIDpref, 0);
                editor.putString(Constants.userNamepref,"false");
                editor.commit();
                finish();
                break;
            case R.id.rank:
                Intent innn = new Intent(this, RankingActivity.class);

                startActivity(innn);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        disconnect();
        moveTaskToBack(true);
    }

    private AdapterView.OnItemClickListener planinaClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Gson gson = new GsonBuilder().serializeNulls().create();
            Planina pl = planine.get(position);
            String plString = gson.toJson(pl);
            Intent i = new Intent(MainActivity.this, PlaninaActivity.class);
            i.putExtra("planina", plString);
            startActivity(i);
        }
    };
}
