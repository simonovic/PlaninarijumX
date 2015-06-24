package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;


public class BluetoothActivity extends Activity
{
    private static final String TAG = "BluetoothActivity";
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private ListView detDevices;
    private BluetoothService mService = null;
    private String deviceName;
    ProgressDialog progressD;
    Button detectBtn;
    private static final int FRIEND_REQUEST = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        progressD = new ProgressDialog(this);
        progressD.setCanceledOnTouchOutside(false);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceName = mBtAdapter.getName()+ " ["+ mBtAdapter.getAddress() + "]";
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        detDevices = (ListView)findViewById(R.id.listView);
        detDevices.setAdapter(mNewDevicesArrayAdapter);
        detDevices.setOnItemClickListener(mDeviceClickListener);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        detectBtn = (Button)findViewById(R.id.detectBtn);
        detDevices.setVisibility(View.GONE);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (!mBtAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 1);
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth je uključen!", Toast.LENGTH_LONG).show();
            if(mService == null)
                mService = new BluetoothService(this, mHandler);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (mService != null)
            if(mService.getState() == BluetoothService.STATE_NONE)
                mService.start();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(mReceiver);

        if (mService != null) {
            mService.stop();
        }
    }

    @Override
    public void onBackPressed()
    {
        if(mBtAdapter.isEnabled())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Isključiti Bluetooth!");
            builder.setPositiveButton("Ne", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            builder.setNegativeButton("Da", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mBtAdapter.disable();
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else
            finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == -1)
                mService = new BluetoothService(this, mHandler);
        }
        else if (requestCode == FRIEND_REQUEST)
        {
            if (resultCode == RESULT_OK)
            {
                Toast.makeText(getApplicationContext(), "Prihvatio, salji serveru i upisi u lokalnu bazu!", Toast.LENGTH_LONG).show();
                String message = "responseYes " + deviceName;
                byte[] send = message.getBytes();
                mService.write(send);
            }
            else if (resultCode == RESULT_CANCELED)
            {
                String message = "responseNo " + deviceName;
                byte[] send = message.getBytes();
                mService.write(send);
            }
        }

    }

    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    switch (msg.arg1)
                    {
                        case 3:
                            Toast.makeText(getApplicationContext(), "Uređaji su povezani!", Toast.LENGTH_LONG).show();
                            break;
                    }
                    break;
                case 2:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    String[] tmp = readMessage.split(" ");
                    if(tmp[0].equals("request"))
                    {
                        Intent i = new Intent(getApplicationContext(), FriendRequestActivity.class);
                        i.putExtra("device", readMessage.substring(8));
                        startActivityForResult(i, FRIEND_REQUEST);
                    }
                    else if (tmp[0].equals("responseYes"))
                    {
                        //salji serveru i upisi u lokalnu bazu
                        RespondeYes(readMessage.substring(12));
                    }
                    else if (tmp[0].equals("responseNo")) {
                        RespondeNo(readMessage.substring(11));
                    }
                    break;
            }
        }
    };

    public void RespondeNo(String message)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle(message + " je odbio zahtev za prijateljstvom!");
        builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder1.create();
        dialog.show();
    }

    public void RespondeYes(String message)
    {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle(message + " je prihvatio zahtev za prijateljstvom!");
        builder2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder2.create();
        dialog.show();
    }

    private void sendMessage(String message)
    {
        if (mService.getState() != mService.STATE_CONNECTED) {
            Toast.makeText(getApplicationContext(), "Uređaji nisu povezani!", Toast.LENGTH_LONG).show();
            return;
        }

        byte[] send = message.getBytes();
        mService.write(send);
    }

    public void onSendRequestBtn(View view)
    {
        sendMessage("request " + deviceName);
        /*Intent i = new Intent(getApplicationContext(), FriendRequestActivity.class);
        i.putExtra("device", deviceName);
        startActivityForResult(i, FRIEND_REQUEST);*/
    }

    public void onDetectBtn(View view)
    {
        // ovde samo za probu
        ensureDiscoverable();

        if(mBtAdapter.isEnabled())
        {
            progressD.setTitle("Bluetooth");
            progressD.setMessage("Detektovanje u toku...");
            progressD.show();

            if(mNewDevicesArrayAdapter.getCount() != 0) {
                mNewDevicesArrayAdapter.clear();
                mNewDevicesArrayAdapter.notifyDataSetChanged();
            }

            if(mBtAdapter.isDiscovering())
                mBtAdapter.cancelDiscovery();

            mBtAdapter.startDiscovery();
        }
        else
            Toast.makeText(getApplicationContext(), "Morate prvo uključiti Bluetooth!", Toast.LENGTH_LONG).show();

    }

    private void ensureDiscoverable()
    {
        if (mBtAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            startActivity(discoverableIntent);
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                progressD.dismiss();
                //mBtAdapter.cancelDiscovery();
                if (mNewDevicesArrayAdapter.getCount() == 0)
                    mNewDevicesArrayAdapter.add("Nijedan uređaj nije pronađen" + "\n" + "Pokušajte ponovo");
                detDevices.setVisibility(View.VISIBLE);
                detectBtn.setClickable(true);
            }
        }
    };

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3)
        {
            mBtAdapter.cancelDiscovery();
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
            mService.connect(device);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_bluetooth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
