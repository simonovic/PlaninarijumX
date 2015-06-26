package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Random;


public class MapActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

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
    private ArrayList<Place> fakeQuest;


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
    }

    private void popuniQuest()
    {
        if(fakeQuest != null)
        {
            fakeQuest.clear();
        }
        fakeQuest = new ArrayList<Place>();
        double lat = MainActivity.myLocation.latitude;
        double lng = MainActivity.myLocation.longitude;

        Random r = new Random();
        int broj = r.nextInt(10);
        for (int i = 0; i < 2; i++)
        {
            double faktor = r.nextDouble();
            faktor = faktor - 0.5;
            faktor = faktor / 2.0;
            Place p = new Place(lat + faktor, lng + faktor,"odg"+i,"pitanje"+i);
            fakeQuest.add(p);
        }
        Place.ID = 0;
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
        map.clear();
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                builder.setMessage("Poruka").setTitle("Izaberi radius u metrima");
                final NumberPicker picker = new NumberPicker(MapActivity.this);

                picker.setMinValue(1);
                picker.setMaxValue(10000);
                builder.setView(picker);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "Idi do servera" + picker.getValue() , Toast.LENGTH_SHORT).show();
                        CircleOptions circleOptions = new CircleOptions().center(MainActivity.myLocation).
                                radius(picker.getValue()).fillColor(0x4033B5E5).strokeColor(0x00000000);//51 181 229
                        map.addCircle(circleOptions);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "Odustao si", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                popuniQuest();
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                quest = new ArrayList<Place>();
                map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(final LatLng latLng) {
                        if(mNavigationDrawerFragment.getmCurrentSelectedPosition() == 3)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                            LayoutInflater inflater = MapActivity.this.getLayoutInflater();
                            final View view = inflater.inflate(R.layout.place_layout,null);
                            builder.setView(view)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            EditText editText = (EditText) view.findViewById(R.id.add_question);
                                            String q = editText.getText().toString();

                                            editText = (EditText) view.findViewById(R.id.add_answer);
                                            String a = editText.getText().toString();


                                            Place place = new Place(latLng.latitude,latLng.longitude,a,q);
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
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                final QuestSolver questSolver = new QuestSolver(fakeQuest);
                Place pitanje = questSolver.getPitanje();
                if(pitanje == null)
                    Toast.makeText(getApplicationContext(), "Cestitam, resio si.", Toast.LENGTH_SHORT)
                            .show();
                map.addMarker(new MarkerOptions().
                        position(new LatLng(pitanje.getLat(),pitanje.getLng()))
                        .title(pitanje.getPitanje()));
                Toast.makeText(getApplicationContext(), "Odgovor:"+pitanje.getOdgovor(), Toast.LENGTH_SHORT).show();
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
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
                                            Place pit = questSolver.getPitanje();
                                            if (pit == null) {
                                                Toast.makeText(getApplicationContext(), "Cestitam, resio si.", Toast.LENGTH_SHORT)
                                                        .show();
                                            } else {
                                                map.addMarker(new MarkerOptions().
                                                        position(new LatLng(pit.getLat(), pit.getLng()))
                                                        .title(pit.getPitanje()));
                                            }
                                        } else
                                            Toast.makeText(getApplicationContext(), "Pogresio si", Toast.LENGTH_SHORT).show();

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

                        return true;
                    }
                });
                break;
        }
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
            }else
                getMenuInflater().inflate(R.menu.map, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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
            Toast.makeText(this,"Posalji serveru",Toast.LENGTH_SHORT).show();
            quest.clear();
            quest = null;
            map.clear();
            mNavigationDrawerFragment.selectItem(0);
            Place.ID = 0;

        }

        return super.onOptionsItemSelected(item);
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
