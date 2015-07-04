package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class PlaninaActivity extends Activity
{
    private String plString;
    private Planina pl;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planina);

        Bundle extras = getIntent().getExtras();
        plString = extras.getString("planina");
        Gson gson = new GsonBuilder().serializeNulls().create();
        pl = gson.fromJson(plString, Planina.class);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                        String request = "9\n"+pl.getId()+"\n";
                        InetAddress adr = InetAddress.getByName(Constants.address);
                        Socket socket = new Socket(adr, Constants.PORT);
                        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
                        printWriter.write(request);
                        printWriter.flush();

                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String serverResponse = in.readLine();

                        printWriter.close();
                        socket.close();

                }
                catch (IOException e) { e.printStackTrace(); }
                }
            }).start();
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
