package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;


public class MyProfileActivity extends Activity
{
    private int userID;
    SharedPreferences shPref;
    private String request;
    EditText ime;
    EditText prezime;
    EditText rang;
    EditText brTel;
    EditText user;
    EditText brPoena;
    OsobaPlus korisnik;
    private String profil;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        ime = (EditText)findViewById(R.id.ime);
        prezime = (EditText)findViewById(R.id.prezime);
        brTel = (EditText)findViewById(R.id.brtel);
        user = (EditText)findViewById(R.id.korIme1);
        rang = (EditText)findViewById(R.id.rang);
        brPoena = (EditText)findViewById(R.id.poeni);

        brTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = ((EditText) v).getText().toString();
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:" + s));
                startActivity(i);
            }
        });

        if (savedInstanceState == null)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        InetAddress adr = InetAddress.getByName(Constants.address);
                        Socket socket = new Socket(adr, Constants.PORT);
                        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
                        request = "1\n" + LogActivity.userID+"\n";
                        printWriter.write(request);
                        printWriter.flush();

                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        profil = in.readLine();
                        Gson gson = new GsonBuilder().serializeNulls().create();
                        korisnik = gson.fromJson(profil, OsobaPlus.class);

                        printWriter.close();
                        socket.close();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ime.setText(korisnik.getIme());
                                prezime.setText(korisnik.getPrezime());
                                user.setText(korisnik.getUser());
                                brTel.setText(korisnik.getBrTelefona());
                                brPoena.setText(korisnik.getBrPoena()+"");
                                rang.setText(korisnik.getRank()+"");
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meny_my_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.quests) {
            Intent i = new Intent(this, QuestsActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
