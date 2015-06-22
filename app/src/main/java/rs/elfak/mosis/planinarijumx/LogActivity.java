package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class LogActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
    }

    public void onPrijavaBtn(View view)
    {
        Toast.makeText(getApplicationContext(), "Radi!", Toast.LENGTH_LONG);
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
                Toast.makeText(getApplicationContext(), "Pokreni activity!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onRegBtn(View view)
    {
        Intent i = new Intent(this, RegistrationActivity.class);
        startActivity(i);
    }
}


