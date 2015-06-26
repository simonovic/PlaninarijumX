package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
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
    private static final String request = "1\n1\n";
    EditText ime;
    EditText prezime;
    EditText rang;
    EditText brTel;
    EditText user;
    EditText brPoena;
    OsobaPlus korisnik;
    private String pl;

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
                    korisnik = gson.fromJson(pl, OsobaPlus.class);

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
