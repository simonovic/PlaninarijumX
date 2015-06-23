package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class FriendRequestActivity extends Activity
{
    String device;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        setFinishOnTouchOutside(false);

        Bundle extras = getIntent().getExtras();
        device = extras.getString("device");
        TextView text = (TextView)findViewById(R.id.textView);
        text.setText("Korisnik " + device + " želi da postanete prijatelji!");
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED,returnIntent);
        finish();
    }

    public void onPrihvatiBtn(View view)
    {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK,returnIntent);
        finish();
    }

    public void onOdbijBtn(View view)
    {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED,returnIntent);
        finish();
    }
}
