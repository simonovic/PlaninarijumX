package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
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
    ImageView image;
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
        image = (ImageView)findViewById(R.id.viewImage);

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
            korisnik = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(korisnik == null)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        InetAddress adr = InetAddress.getByName(Constants.address);
                        Socket socket = new Socket(adr, Constants.PORT);
                        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);

                        if (getIntent().hasExtra("friendsID"))
                        {
                            Bundle extras = getIntent().getExtras();
                            String friendsID = extras.getString("friendsID");
                            request = "1\n" + friendsID + "\n";
                        }
                        else
                            request = "1\n" + LogActivity.userID + "\n";

                        printWriter.write(request);
                        printWriter.flush();

                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        profil = in.readLine();
                        Gson gson = new GsonBuilder().serializeNulls().create();
                        korisnik = gson.fromJson(profil, OsobaPlus.class);

                        int totalBytesRead = 0;
                        int fileSize = Integer.parseInt(in.readLine());
                        byte[] data = null;
                        InputStream istream = socket.getInputStream();
                        data = new byte[fileSize];

                        while (totalBytesRead < fileSize)
                        {
                            int bytesRemaining = fileSize - totalBytesRead;
                            int bytesRead;
                            bytesRead = istream.read(data, totalBytesRead, bytesRemaining);
                            if (bytesRead == -1)
                            {
                                break; // socket has been closed
                            }

                            totalBytesRead += bytesRead;
                        }

                        final Bitmap bMap = BitmapFactory.decodeByteArray(data, 0, data.length);

                        in.close();
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
                                image.setImageBitmap(bMap);
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
    protected void onResume() {
        super.onResume();
        LogActivity.trenutnaAktivnost = this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.quests) {
            Intent i = new Intent(this, QuestsActivity.class);
            i.putExtra("userID", korisnik.getId());
            startActivity(i);
        }
        else if (id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
