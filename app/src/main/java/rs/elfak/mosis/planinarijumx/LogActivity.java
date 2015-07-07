package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class LogActivity extends Activity
{
    SharedPreferences shPref;
    static int userID;
    static String userName;
    static Activity trenutnaAktivnost;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_log);
        shPref = getSharedPreferences(Constants.loginpref, Context.MODE_PRIVATE);
        userID = shPref.getInt(Constants.userIDpref, 0);
        userName = shPref.getString(Constants.userNamepref,"false");
        SharedPreferences.Editor editor = shPref.edit();
        editor.putInt(Constants.userIDpref, userID);
        editor.commit();
        if ((userID != 0) && (!userName.equals("false")))
        {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("userID", userID);
            startActivity(i);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        trenutnaAktivnost = this;
        EditText loz = (EditText)findViewById(R.id.lozinka);
        loz.setText("");
    }

    public void onPrijavaBtn(View view)
    {
        final EditText ime = (EditText)findViewById(R.id.korIme);
        final EditText loz = (EditText)findViewById(R.id.lozinka);
        if(ime.getText().toString().trim().equals("") || loz.getText().toString().trim().equals(""))
        {
            Toast.makeText(getApplicationContext(), "Morate uneti i korisničko ime i lozinku!", Toast.LENGTH_LONG).show();
        }
        else
        {

            new Thread(new Runnable() {
                @Override
                public void run() {


                    int response;
                    response = 0;

                    try {
                        InetAddress adr = InetAddress.getByName(Constants.address);
                        Socket socket = new Socket(adr, Constants.PORT);
                        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                        String sendBuf = "6\n";
                        sendBuf += ime.getText().toString();
                        sendBuf += "\n" + loz.getText().toString() + "\n";
                        printWriter.write(sendBuf);
                        printWriter.flush();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String prijem = in.readLine();
                        response = Integer.parseInt(prijem);
                        in.close();
                        printWriter.close();
                        socket.close();

                        if (response <= 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            "Pogrešno korisničko ime i/ili lozinka!", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else {
                            SharedPreferences.Editor editor = shPref.edit();
                            userID = response;
                            editor.putInt(Constants.userIDpref, userID);
                            editor.putString(Constants.userNamepref,ime.getText().toString());
                            editor.commit();
                            userID = shPref.getInt(Constants.userIDpref, 0);
                            Intent i = new Intent(LogActivity.this, MainActivity.class);

                            startActivity(i);
                        }
                    }
                    catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();


        }
    }

    public void onRegBtn(View view)
    {
        Intent i = new Intent(this, RegistrationActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}


