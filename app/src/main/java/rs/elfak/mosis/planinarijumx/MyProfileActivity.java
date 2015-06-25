package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


public class MyProfileActivity extends Activity
{
    private int userID;
    SharedPreferences shPref;
    private static final String request = "5\nplanine\n";  //promeni da odg profilu sam
    EditText ime;
    EditText prezime;
    EditText rang;
    EditText brTel;
    EditText user;
    EditText brPoena;

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

            }
        }).start();
    }

}
