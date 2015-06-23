package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;


public class MainActivity extends Activity
{

    Handler handler;
    Runnable runnable;
    LatLng myLocation = new LatLng(43.319425, 21.899487);
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Toast.makeText(getApplicationContext(),"Update sam lokaciju" + myLocation.latitude
                        + "  " + myLocation.longitude, Toast.LENGTH_SHORT).show();
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
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter == null)
                    Toast.makeText(getApplicationContext(), "Uređaj ne podržava Bluetooth!", Toast.LENGTH_LONG).show();
                else
                {
                    Intent in = new Intent(this, BluetoothActivity.class);
                    startActivity(in);
                }
                break;
            case R.id.profil:
                break;
            case R.id.logout:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
