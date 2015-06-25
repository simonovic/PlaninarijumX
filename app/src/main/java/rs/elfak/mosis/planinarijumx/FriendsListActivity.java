package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;


public class FriendsListActivity extends Activity
{
    private ArrayAdapter<String> friendsAdapter;
    ListView friendsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        friendsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        friendsList = (ListView)findViewById(R.id.listFriends);
        friendsList.setAdapter(friendsAdapter);
        friendsAdapter.add("1.");
        friendsAdapter.add("2.");
        friendsAdapter.add("3.");
        friendsAdapter.add("4.");
        friendsAdapter.add("5.");
    }

    public ArrayAdapter<String> getFriendsAdapter() {
        return friendsAdapter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_friends_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.friend)
        {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null)
                Toast.makeText(getApplicationContext(), "Uređaj ne podržava Bluetooth!", Toast.LENGTH_LONG).show();
            else
            {
                Intent in = new Intent(this, BluetoothActivity.class);
                startActivity(in);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
