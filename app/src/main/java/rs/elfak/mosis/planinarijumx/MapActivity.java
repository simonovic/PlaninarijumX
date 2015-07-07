package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;


public class MapActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks
{
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private GoogleMap map;
    private ArrayList<Place> quest;
    private String questName = "";
    private ArrayList<Place> fakeQuest;
    private int planinaID;
    private QuestSolver questSolver = null;
    private ArrayList<OnlinePrijatelj> onlineFriends;
    private ArrayList<Mesto> placesInRadius;
    private ArrayList<OsobaRadiQuest> usersQuests;
    private HashMap<Marker, Boolean> resenaPitanja;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        DrawerLayout mDrawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerLayout.closeDrawers();



        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        if(map != null)
            map.setMyLocationEnabled(true);

        resenaPitanja = new HashMap<>();

        Intent i = getIntent();
        planinaID = i.getIntExtra("planinaID",-3);
        if(planinaID != -3)
        {
            mNavigationDrawerFragment.setMenuVisibility(false);
            mNavigationDrawerFragment.selectItem(3);
            return;
        }

        String kvizovi = i.getStringExtra("questInfo");
        if(kvizovi != null) {
            startZaSimona(kvizovi);
        }

    }

    private void startZaSimona(String informacije)
    {
        String [] linije = informacije.split("\n");
        questName = linije[0];
        int questID = Integer.parseInt(linije[1]);
        int pozicija = Integer.parseInt(linije[2]);
        Gson gson = new GsonBuilder().serializeNulls().create();
        ArrayList<NovoMesto> mestaUKvizu = gson.fromJson(linije[3], new TypeToken<ArrayList<NovoMesto>>() {}.getType());
        quest = new ArrayList<Place>();
        Place.ID = 1;
        for(int j = 0; j < mestaUKvizu.size(); j++)
        {
            NovoMesto novoMesto = mestaUKvizu.get(j);
            Place p = new Place(novoMesto.getLat(),novoMesto.getLon(),novoMesto.getOdgovor(),novoMesto.getPitanje(),novoMesto.getRedBroj());
            quest.add(p);
        }
        questSolver = new QuestSolver(quest,questID);
        if(pozicija != -1)
        {
            questSolver.setPosition(pozicija);
            questSolver.setZapocet(true);
        }else
        {
            questSolver.setPosition(1);
        }
        mNavigationDrawerFragment.setMenuVisibility(false);
        mNavigationDrawerFragment.selectItem(4);
        return;

    }

    private void prikaziQuest()
    {
        if(!questSolver.isZapocet())
            for(int i = 0 ; i < quest.size(); i++)
            {
                LatLng latLng = new LatLng(quest.get(i).getLat(), quest.get(i).getLng());
                if(quest.get(i).getId() != quest.size()) {
                    Marker marker = map.addMarker(new MarkerOptions().position(latLng).
                            title(("Cilj")));

                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_monno));
                    resenaPitanja.put(marker,false);
                }
                else
                {
                    Marker marker = map.addMarker(new MarkerOptions().position(latLng).
                            title((quest.get(i).getId()) + ". " + getString(R.string.question)));

                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_finishno));
                    resenaPitanja.put(marker, false);
                }
            }
        else if(questSolver.getPosition() == 0)
        {
            for(int i = 0 ; i < quest.size(); i++)
            {
                LatLng latLng = new LatLng(quest.get(i).getLat(), quest.get(i).getLng());
                if(quest.get(i).getId() != quest.size()) {
                    Marker marker = map.addMarker(new MarkerOptions().position(latLng).
                            title((quest.get(i).getId() + ". " + getString(R.string.question))));

                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_monyes));
                    resenaPitanja.put(marker, true);
                }
                else
                {
                    Marker marker = map.addMarker(new MarkerOptions().position(latLng).
                            title("Cilj"));

                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_finishyes));
                    resenaPitanja.put(marker, true);
                }
            }
        }else
        {
            for(int i = 0; i < quest.size(); i++)
            {
                LatLng latLng = new LatLng(quest.get(i).getLat(), quest.get(i).getLng());
                if(questSolver.getPosition() > quest.get(i).getId()) {
                    Marker marker = map.addMarker(new MarkerOptions().position(latLng).
                            title((quest.size() - quest.get(i).getId()) + ". " + getString(R.string.question)));

                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_monyes));
                    resenaPitanja.put(marker, true);
                }
            }
            radiQuest();
        }

    }

    public static ArrayList<OnlinePrijatelj> getPrijatelji()
    {
        ArrayList<OnlinePrijatelj> onlinePrijatelji = null;

        try {
            String sendBuff = "12\n" + LogActivity.userID + "\n";
            InetAddress adr = InetAddress.getByName(Constants.address);
            Socket socket = new Socket(adr, Constants.PORT);
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.write(sendBuff);
            printWriter.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String info = in.readLine();
            Gson gson = new GsonBuilder().serializeNulls().create();
            onlinePrijatelji = gson.fromJson(info, new TypeToken<ArrayList<OnlinePrijatelj>>() {
            }.getType());

            in.close();
            printWriter.close();
            socket.close();


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return onlinePrijatelji;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(final int number) {
        invalidateOptionsMenu();
        if(map != null)
            map.clear();
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                LatLng latLng = new LatLng(MainActivity.MyLocation.latitude,MainActivity.MyLocation.longitude);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                builder.setMessage("Poruka").setTitle("Izaberite radjius");
                final NumberPicker picker = new NumberPicker(MapActivity.this);
                picker.setMinValue(1);
                picker.setMaxValue(10000);
                builder.setView(picker);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final int radius = picker.getValue();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    String request = "15\n" + LogActivity.userID + "\n" + MainActivity.MyLocation.latitude + "\n" + MainActivity.MyLocation.longitude + "\n" + 10000000 + "\n";
                                    InetAddress adr = InetAddress.getByName(Constants.address);
                                    Socket socket = new Socket(adr, Constants.PORT);
                                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
                                    printWriter.write(request);
                                    printWriter.flush();

                                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                    String response = in.readLine();
                                    Gson gson = new GsonBuilder().serializeNulls().create();
                                    onlineFriends = gson.fromJson(response, new TypeToken<ArrayList<OnlinePrijatelj>>() {}.getType());

                                    response = in.readLine();
                                    placesInRadius = gson.fromJson(response, new TypeToken<ArrayList<Mesto>>() {}.getType());

                                    response = in.readLine();
                                    usersQuests = gson.fromJson(response, new TypeToken<ArrayList<OsobaRadiQuest>>() {}.getType());

                                    printWriter.close();
                                    socket.close();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            for (int i = 0; i < onlineFriends.size(); i++) {
                                                LatLng latLng = new LatLng(onlineFriends.get(i).getLat(), onlineFriends.get(i).getLon());
                                                map.addMarker(new MarkerOptions().position(latLng).title("user").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_user)));
                                            }

                                            int pom;
                                            int userQID;
                                            int mestoQID;
                                            int ddd = 0;
                                            for (int i = 0; i < placesInRadius.size(); i++) {

                                                boolean bingo = false;
                                                int br = 0;
                                                mestoQID = placesInRadius.get(i).getQuestId();
                                                if (usersQuests.size() > 0)
                                                {
                                                    do {
                                                        userQID = usersQuests.get(br).getQuestId();
                                                        if (mestoQID == userQID)
                                                            bingo = true;
                                                        ddd = usersQuests.get(br).getMestoBr();
                                                        br++;
                                                    } while ((mestoQID != userQID) && (br < usersQuests.size()));
                                                }

                                                if (bingo)
                                                {
                                                    if (placesInRadius.get(i).getRedBroj() < ddd)
                                                        pom = R.mipmap.ic_monyes;
                                                    else
                                                        pom = R.mipmap.ic_monno;
                                                }
                                                else
                                                    pom = R.mipmap.ic_monnoq;

                                                LatLng latLng = new LatLng(placesInRadius.get(i).getLat(), placesInRadius.get(i).getLon());
                                                map.addMarker(new MarkerOptions().position(latLng).title("" + mestoQID).icon(BitmapDescriptorFactory.fromResource(pom)));
                                            }

                                            CircleOptions circleOptions = new CircleOptions().center(MainActivity.MyLocation).radius(100).fillColor(0x4033B5E5).strokeColor(0x00000000);//51 181 229
                                            map.addCircle(circleOptions);

                                            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                                @Override
                                                public boolean onMarkerClick(Marker marker)
                                                {
                                                    Toast.makeText(MapActivity.this, "Klik na moj marker!", Toast.LENGTH_LONG).show();
                                                    return false;
                                                }
                                            });
                                        }
                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
                builder.setNegativeButton("Otkaži", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Toast.makeText(getApplicationContext(), "Odustao si", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            case 3:
                mTitle = getString(R.string.title_section3);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final ArrayList<OnlinePrijatelj> onlinePrijatelji = MapActivity.getPrijatelji();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for(int i = 0 ; i < onlinePrijatelji.size(); i++)
                                {
                                    LatLng latLng = new LatLng(onlinePrijatelji.get(i).getLat(),onlinePrijatelji.get(i).getLon());
                                    map.addMarker(new MarkerOptions().position(latLng).title((onlinePrijatelji.get(i).getUser())))
                                            .setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_user));
                                }
                            }
                        });
                    }
                }).start();

                break;
            case 4:
                mTitle = getString(R.string.title_section4);

                if(planinaID == -3)
                {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                InetAddress adr = InetAddress.getByName(Constants.address);
                                Socket socket = new Socket(adr, Constants.PORT);
                                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                                printWriter.write("5\n");
                                printWriter.flush();

                                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                String pl = in.readLine();
                                Gson gson = new GsonBuilder().serializeNulls().create();
                                final ArrayList<Planina> planine = gson.fromJson(pl, new TypeToken<ArrayList<Planina>>() {
                                }.getType());

                                printWriter.close();
                                socket.close();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(MapActivity.this);
                                        builder2.setTitle("Izaberi prijatelja");
                                        String[] imenaPlanina = new String[planine.size()];
                                        for (int i = 0; i < planine.size(); i++) {
                                            imenaPlanina[i] = planine.get(i).getIme();
                                        }
                                        builder2.setItems(imenaPlanina, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(getApplicationContext(), planine.get(which).getIme(),
                                                        Toast.LENGTH_SHORT).show();
                                                map.moveCamera(CameraUpdateFactory.
                                                        newLatLngZoom(new LatLng(planine.get(which).getLat(), planine.get(which).getLon()), 17));
                                                dodavanjeKviza(planine.get(which).getId());
                                            }
                                        });

                                        AlertDialog alertDialog = builder2.create();
                                        alertDialog.show();
                                    }
                                });
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
                else
                    dodavanjeKviza(planinaID);


                break;
            case 5:
                mTitle = getString(R.string.title_section5) + " : " + questName;
                prikaziQuest();

                break;
        }
    }

    private void dodavanjeKviza(int planina)
    {
        planinaID = planina;
        AlertDialog.Builder builderName = new AlertDialog.Builder(MapActivity.this);
        builderName.setTitle("Naziv kviza");
        final EditText imeKviza = new EditText(MapActivity.this);
        imeKviza.setHint("Naziv kviza");
        builderName.setView(imeKviza);
        builderName.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (planinaID != -3)
                    finish();
                else
                    mNavigationDrawerFragment.selectItem(0);
            }
        });
        builderName.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                questName = imeKviza.getText().toString();
            }
        });
        AlertDialog alertDia = builderName.create();
        alertDia.setCanceledOnTouchOutside(false);
        alertDia.show();
        quest = new ArrayList<Place>();
        if(map != null)
            map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(final LatLng latLng) {
                    if (mNavigationDrawerFragment.getmCurrentSelectedPosition() == 3) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                        LayoutInflater inflater = MapActivity.this.getLayoutInflater();
                        final View view = inflater.inflate(R.layout.place_layout, null);
                        builder.setView(view)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        EditText editText = (EditText) view.findViewById(R.id.add_question);
                                        String q = editText.getText().toString();

                                        editText = (EditText) view.findViewById(R.id.add_answer);
                                        String a = editText.getText().toString();

                                        Place place = new Place(latLng.latitude, latLng.longitude, a, q, Place.ID++);
                                        quest.add(place);
                                        map.addMarker(new MarkerOptions().position(latLng).title(q));
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(getApplicationContext(), "Odustao si", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    }
                }
            });
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            if(mNavigationDrawerFragment.getmCurrentSelectedPosition() == 3)
            {
                getMenuInflater().inflate(R.menu.add_quest,menu);
            }else if((mNavigationDrawerFragment.getmCurrentSelectedPosition() == 4)
                    && (questSolver != null)
                    && (!questSolver.isZapocet()))
            {
                getMenuInflater().inflate(R.menu.kviz_menu,menu);
            }
            else
                getMenuInflater().inflate(R.menu.map, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void zapocniQuest()
    {
        if(!questSolver.isZapocet()) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String sendBuff = "14\n" + LogActivity.userID + "\n" + questSolver.getQuestID() + "\n";
                    try {
                        InetAddress adr = InetAddress.getByName(Constants.address);
                        Socket socket = new Socket(adr, Constants.PORT);
                        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                        printWriter.write(sendBuff);
                        printWriter.flush();

                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        final String pitanje = in.readLine();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(pitanje.equals("false"))
                                    Toast.makeText(getApplicationContext(),"Neuspelo zapocinjanje kviza", Toast.LENGTH_SHORT).show();
                                else if(pitanje.equals("true"))
                                {
                                    map.clear();

                                    questSolver.setZapocet(true);
                                    invalidateOptionsMenu();
                                    questSolver.setPosition(1);
                                    resenaPitanja = new HashMap<Marker, Boolean>();
                                    radiQuest();




                                }
                            }
                        });
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        }else
        {
            map.clear();
            radiQuest();
        }

    }

    private void radiQuest()
    {
        Place pitanje = questSolver.getPitanje();
        if (pitanje == null)
            Toast.makeText(getApplicationContext(), "Cestitam, resio si.", Toast.LENGTH_SHORT)
                    .show();
        if(pitanje.getId() == quest.size()) {
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(pitanje.getLat(), pitanje.getLng()))
                    .title(pitanje.getPitanje()));
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_finishno));

            resenaPitanja.put(marker,false);

        }
        else {
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(pitanje.getLat(), pitanje.getLng()))
                    .title(pitanje.getPitanje()));
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_monno));

            resenaPitanja.put(marker, false);
        }

        LatLng latLng = new LatLng(pitanje.getLat(),pitanje.getLng());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));

        Toast.makeText(getApplicationContext(), "Odgovor:" + pitanje.getOdgovor(), Toast.LENGTH_SHORT).show();
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if(resenaPitanja.containsKey(marker))
                    if(resenaPitanja.get(marker)) {
                        Toast.makeText(getApplicationContext(), "Vec si resio ovo pitanje", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                final AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                LayoutInflater inflater = MapActivity.this.getLayoutInflater();
                final View view = inflater.inflate(R.layout.place_layout, null);
                ((EditText) view.findViewById(R.id.add_question)).setText(marker.getTitle());
                ((EditText) view.findViewById(R.id.add_question)).setKeyListener(null);
                builder.setView(view)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String odg = ((EditText) view.findViewById(R.id.add_answer)).getText().toString();
                                boolean b = questSolver.Solve(odg);
                                if (b == true) {
                                    final Place pit = questSolver.getPitanje();
                                    resenaPitanja.remove(marker);
                                    if (pit == null) {

                                        updateMarker(marker,true);
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run()
                                            {
                                                try {
                                                    String sendBuff = "13\n" + LogActivity.userID +
                                                            "\n" + questSolver.getQuestID() + "\n" +
                                                            0 + "\n"
                                                            + 100 + "\n";
                                                    InetAddress adr = InetAddress.getByName(Constants.address);
                                                    Socket socket = new Socket(adr, Constants.PORT);
                                                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                                                    printWriter.write(sendBuff);
                                                    printWriter.flush();

                                                    printWriter.close();
                                                    socket.close();
                                                } catch (UnknownHostException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }).start();
                                        Toast.makeText(getApplicationContext(), "Cestitam, resio si.", Toast.LENGTH_SHORT)
                                                .show();
                                    } else {
                                        updateMarker(marker,false);
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run()
                                            {
                                                try {
                                                    String sendBuff = "13\n" + LogActivity.userID +
                                                            "\n" + questSolver.getQuestID() + "\n" +
                                                            (questSolver.getQuest().size() - questSolver.getPosition()) + "\n"
                                                            + 10 + "\n";
                                                    InetAddress adr = InetAddress.getByName(Constants.address);
                                                    Socket socket = new Socket(adr, Constants.PORT);
                                                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                                                    printWriter.write(sendBuff);
                                                    printWriter.flush();

                                                    printWriter.close();
                                                    socket.close();
                                                } catch (UnknownHostException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }).start();
                                        if(pit.getId() == quest.size()) {
                                            Marker mark = map.addMarker(new MarkerOptions()
                                                    .position(new LatLng(pit.getLat(), pit.getLng()))
                                                    .title(pit.getPitanje()));
                                            mark.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_finishno));

                                            resenaPitanja.put(mark, false);
                                        }
                                        else {
                                            Marker mark = map.addMarker(new MarkerOptions()
                                                    .position(new LatLng(pit.getLat(), pit.getLng()))
                                                    .title(pit.getPitanje()));
                                            mark.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_monno));

                                            resenaPitanja.put(mark, false);
                                        }

                                        LatLng latLng = new LatLng(pit.getLat(),pit.getLng());
                                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                                    }
                                } else
                                    Toast.makeText(getApplicationContext(), "Pogresio si", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Toast.makeText(getApplicationContext(), "Odustao si", Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.setNeutralButton(getString(R.string.ask_friend), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final ArrayList<OnlinePrijatelj> onlinePrijatelji = MapActivity.getPrijatelji();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (onlinePrijatelji.size() == 0) {
                                            Toast.makeText(getApplicationContext(), "Nemas prijatelje online", Toast.LENGTH_SHORT).show();
                                            return;
                                        } else {
                                            AlertDialog.Builder builder2 = new AlertDialog.Builder(MapActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                                            builder2.setTitle("Izaberi prijatelja");
                                            String[] imenaPrijatelja = new String[onlinePrijatelji.size()];
                                            for (int i = 0; i < onlinePrijatelji.size(); i++) {
                                                imenaPrijatelja[i] = onlinePrijatelji.get(i).getUser();
                                            }
                                            builder2.setItems(imenaPrijatelja, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Toast.makeText(getApplicationContext(), onlinePrijatelji.get(which).getUser(),
                                                            Toast.LENGTH_SHORT).show();
                                                    pitajPrijatelja(onlinePrijatelji.get(which), marker);
                                                }
                                            });

                                            AlertDialog alertDialog = builder2.create();
                                            alertDialog.show();
                                        }

                                    }
                                });
                            }
                        }).start();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
        });
    }

    private void updateMarker(Marker marker,boolean cilj)
    {

        String title;
        double lat,lon;
        title = marker.getTitle();
        lat = marker.getPosition().latitude;
        lon = marker.getPosition().longitude;
        marker.remove();

        LatLng latLng = new LatLng(lat,lon);
        if(!cilj) {
            Marker marker1 = map.addMarker(new MarkerOptions().position(latLng)
                    .title(title));
            marker1.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_monyes));
            resenaPitanja.put(marker1,true);
        }
        else {
            Marker marker1 = map.addMarker(new MarkerOptions().position(latLng)
                    .title(title));
            marker1.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_monyes));
            resenaPitanja.put(marker1, true);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.action_done_adding_places)
        {
            //Toast.makeText(this,"Posalji serveru",Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {

                    String sendBuf = "7\n" + questName + "\n" + planinaID + "\n" + LogActivity.userID + "\n";
                    sendBuf += quest.size() + "\n";

                    try {
                        InetAddress  adr = InetAddress.getByName(Constants.address);

                        Socket socket = new Socket(adr, Constants.PORT);
                        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                        printWriter.write(sendBuf);
                        for(int i = 0; i < quest.size(); i++)
                        {
                            Place p = quest.get(i);
                            NovoMesto novoMesto = new NovoMesto(-1,p.getPitanje(),p.getOdgovor(),p.getLat(),p.getLng(),p.getId());
                            sendBuf = novoMesto.toString() + "\n";
                            printWriter.write(sendBuf);
                        }
                        printWriter.flush();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        final String prijem = in.readLine();
                        printWriter.close();
                        in.close();
                        socket.close();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(prijem != null) {
                                    if (prijem.equals("false"))
                                        Toast.makeText(MapActivity.this
                                                , "Neuspelo kreiranje kviza", Toast.LENGTH_SHORT).show();
                                    else
                                    {
                                        Toast.makeText(MapActivity.this
                                                , "Uspesno kreiranje kviza", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                quest.clear();
                                quest = null;
                                Place.ID = 1;
                                if(map != null)
                                    map.clear();
                                if(planinaID != -3)
                                    finish();
                                else
                                    mNavigationDrawerFragment.selectItem(0);
                            }
                        });
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();

        }else if(id == R.id.start_quest)
        {
            zapocniQuest();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogActivity.trenutnaAktivnost = this;
    }

    private void pitajPrijatelja(final OnlinePrijatelj onlinePrijatelj,final Marker marker)
    {
        final String pitanje;
        final double lat,lon;
        pitanje = marker.getTitle();
        lat = marker.getPosition().latitude;
        lon = marker.getPosition().longitude;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String adresa = onlinePrijatelj.getIp().substring(1);
                StringTokenizer tokeni = new StringTokenizer(adresa, ":");
                adresa = tokeni.nextToken();
                String sendBuf = pitanje + "\n" +
                        lat + "\n" + lon + "\n" + LogActivity.userName + "\n";

                try {
                    InetAddress adr = InetAddress.getByName(adresa);

                    Socket socket = new Socket(adr, Constants.FRIENDPORT);
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                    printWriter.write(sendBuf);
                    printWriter.flush();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    final String odgovor = in.readLine();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                            LayoutInflater inflater = MapActivity.this.getLayoutInflater();
                            final View view = inflater.inflate(R.layout.place_layout, null);
                            ((EditText) view.findViewById(R.id.add_question)).setText(marker.getTitle());
                            ((EditText) view.findViewById(R.id.add_question)).setKeyListener(null);
                            ((EditText) view.findViewById(R.id.add_answer)).setText(odgovor);
                            ((EditText) view.findViewById(R.id.add_answer)).setKeyListener(null);

                            builder.setView(view);

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    boolean b = questSolver.Solve(odgovor);
                                    if (b == true) {
                                        resenaPitanja.remove(marker);
                                        final Place pit = questSolver.getPitanje();
                                        if (pit == null) {
                                            updateMarker(marker, true);
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        String sendBuff = "13\n" + LogActivity.userID +
                                                                "\n" + questSolver.getQuestID() + "\n" +
                                                                0 + "\n"
                                                                + 0 + "\n";
                                                        InetAddress adr = InetAddress.getByName(Constants.address);
                                                        Socket socket = new Socket(adr, Constants.PORT);
                                                        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                                                        printWriter.write(sendBuff);
                                                        printWriter.flush();

                                                        printWriter.close();
                                                        socket.close();
                                                    } catch (UnknownHostException e) {
                                                        e.printStackTrace();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }).start();
                                            Toast.makeText(getApplicationContext(), "Cestitam, resio si.", Toast.LENGTH_SHORT)
                                                    .show();
                                        } else {
                                            updateMarker(marker, false);
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        String sendBuff = "13\n" + LogActivity.userID +
                                                                "\n" + questSolver.getQuestID() + "\n" +
                                                                (pit.getId()) + "\n"
                                                                + 0 + "\n";
                                                        InetAddress adr = InetAddress.getByName(Constants.address);
                                                        Socket socket = new Socket(adr, Constants.PORT);
                                                        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                                                        printWriter.write(sendBuff);
                                                        printWriter.flush();

                                                        printWriter.close();
                                                        socket.close();
                                                    } catch (UnknownHostException e) {
                                                        e.printStackTrace();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }).start();
                                            if (pit.getId() == quest.size()) {

                                                Marker mar = map.addMarker(new MarkerOptions()
                                                        .position(new LatLng(pit.getLat(), pit.getLng()))
                                                        .title(pit.getPitanje()));
                                                mar.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_finishno));
                                                resenaPitanja.put(mar,false);
                                            }
                                            else {
                                                Marker mar = map.addMarker(new MarkerOptions()
                                                        .position(new LatLng(pit.getLat(), pit.getLng()))
                                                        .title(pit.getPitanje()));
                                                mar.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_monno));
                                                resenaPitanja.put(mar, false);
                                            }

                                            LatLng latLng = new LatLng(pit.getLat(),pit.getLng());
                                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

                                        }
                                    }else
                                        Toast.makeText(getApplicationContext(), "Prijatelj pogresio", Toast.LENGTH_SHORT).show();

                                }
                            });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();
                        }
                    });

                    in.close();
                    printWriter.close();
                    socket.close();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_map, container, false);

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MapActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
