package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class LogActivity extends Activity
{
    public static final String loginpref = "LoginPref";
    public static final String userpref = "user";
    SharedPreferences shPref;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        shPref = getSharedPreferences(loginpref, Context.MODE_PRIVATE);
        username = shPref.getString(userpref, null);
        if ((username != "") && (username != null))
        {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("user", username);
            startActivity(i);
        }
    }

    public void onPrijavaBtn(View view)
    {
        EditText ime = (EditText)findViewById(R.id.korIme);
        EditText loz = (EditText)findViewById(R.id.lozinka);
        if(ime.getText().toString().trim().equals("") || loz.getText().toString().trim().equals(""))
        {
            //Toast.makeText(getApplicationContext(), "Morate uneti i korisničko ime i lozinku!", Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
        else
        {
            // provera na sarveru
            int response;
            response = 1;
            //////////////////////

            if(response == 0)
                Toast.makeText(getApplicationContext(), "Pogrešno korisničko ime i/ili lozinka!", Toast.LENGTH_LONG).show();
            else
            {
                SharedPreferences.Editor editor = shPref.edit();
                username = ime.getText().toString();
                editor.putString(userpref, username);
                editor.commit();
                username = shPref.getString(userpref, null);
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("user", username);
                startActivity(i);
            }
        }
    }

    public void onRegBtn(View view)
    {
        Intent i = new Intent(this, RegistrationActivity.class);
        startActivity(i);
    }
}


