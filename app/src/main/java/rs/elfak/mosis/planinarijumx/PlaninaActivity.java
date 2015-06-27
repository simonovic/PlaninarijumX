package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class PlaninaActivity extends Activity
{
    String plString;
    Planina pl;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planina);

        Bundle extras = getIntent().getExtras();
        plString = extras.getString("planina");
        Gson gson = new GsonBuilder().serializeNulls().create();
        pl = gson.fromJson(plString, Planina.class);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_planina, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_dodaj_kviz_na_planini)
        {
            Intent i = new Intent(this,MapActivity.class);
            i.putExtra("planinaID",pl.getId());
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
