package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

//0 - Registracija
public class MainActivity extends Activity
{
    String username;
    SharedPreferences shPref;
    public static final String loginpref = "LoginPref";
    public static final String userpref = "user";
    public static final String passpref = "pass";
    public static final String address = "192.168.1.10";
    public static final int PORT = 4444;
    Handler handler;
    Runnable runnable;
    static LatLng myLocation = new LatLng(43.319425, 21.899487);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shPref = getSharedPreferences(loginpref, Context.MODE_PRIVATE);

        //Bundle extras = getIntent().getExtras();
        //username = extras.getString("user");

        /*handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"Posalji serveru lokaciju", Toast.LENGTH_SHORT).show();
                handler.postDelayed(runnable, 13000);
            }
        };
        handler.postDelayed(runnable, 13000);*/;
    }

    protected void onStart() {
        super.onStart();
        final GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                myLocation = new LatLng(location.getLatitude(),location.getLongitude());
                //Toast.makeText(getApplicationContext(),"Update sam lokaciju" + myLocation.latitude
                        //+ "  " + myLocation.longitude, Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //handler.removeCallbacks(runnable);
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
                editor.putString(userpref, null);
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
}
