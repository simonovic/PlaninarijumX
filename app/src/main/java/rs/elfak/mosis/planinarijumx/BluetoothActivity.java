package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
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
    int userID;
    SharedPreferences shPref;
    private boolean sender = false;
    private ArrayAdapter<String> friendsIDAdapter;
    String friendsID;
    String[] friendsIDs;

    private String[] str;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        progressD = new ProgressDialog(this);
        progressD.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP /*&& !event.isCanceled()*/)
                {

                }
                return true;
            }
        });

        //friendsIDAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

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

        shPref = getSharedPreferences(Constants.loginpref, Context.MODE_PRIVATE);
        userID = shPref.getInt(Constants.userIDpref, 0);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (!mBtAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 1);
        } else {
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
        if (progressD.isShowing())
              Toast.makeText(getApplicationContext(), "onBackPressed progressD isShowing()!", Toast.LENGTH_LONG).show();
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
                            if (sender) {
                                sender = false;
                                Toast.makeText(getApplicationContext(), "Uređaji su povezani!", Toast.LENGTH_LONG).show();
                                String pom = "requestID";
                                byte[] pom1 = pom.getBytes();
                                mService.write(pom1);
                            }
                            break;
                    }
                    break;
                case 2:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    String[] tmp = readMessage.split(" ");
                    if(tmp[0].equals("requestID"))
                    {
                        String pom = "responseID " + userID;
                        byte[] pom1 = pom.getBytes();
                        mService.write(pom1);
                    }
                    else if (tmp[0].equals("responseID"))
                    {
                        //Provera sa lokalnom bazom da li su prijatelji, ako jesu
                        String pom = "request " + deviceName;
                        byte[] pom1 = pom.getBytes();
                        mService.write(pom1);
                    }
                    else if (tmp[0].equals("request"))
                    {
                        /*Intent i = new Intent(BluetoothActivity.this, FriendRequestActivity.class);
                        i.putExtra("device", readMessage.substring(8));
                        startActivityForResult(i, FRIEND_REQUEST);*/

                        AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothActivity.this);
                        builder.setTitle("Prijateljstvo!");
                        builder.setMessage("Korisnik " + readMessage.substring(8) + " želi da postanete prijatelji!");
                        builder.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String pom = "responseYes " + deviceName;
                                byte[] pom1 = pom.getBytes();
                                mService.write(pom1);
                            }
                        });
                        builder.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String pom = "responseNo " + deviceName;
                                byte[] pom1 = pom.getBytes();
                                mService.write(pom1);
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
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

    public void onDetectBtn(View view)
    {


        if(mBtAdapter.isEnabled())
        {
            ensureDiscoverable();

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
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 360);
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
                if (mNewDevicesArrayAdapter.getCount() == 0)
                    Toast.makeText(BluetoothActivity.this, "Nijedan uređaj nije pronađen!", Toast.LENGTH_LONG).show();
                    //mNewDevicesArrayAdapter.add("Nijedan uređaj nije pronađen" + "\n" + "Pokušajte ponovo");
                detDevices.setVisibility(View.VISIBLE);
                detectBtn.setClickable(true);
            }
        }
    };

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3)
        {
            sender = true;
            String info = ((TextView) v).getText().toString();
            str = info.split("\n");

            AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothActivity.this);
            builder.setTitle("Prijateljstvo");
            builder.setMessage("Poslati zahtev korisniku " + str[0] + " [" + str[1] + "]?");
            builder.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mBtAdapter.cancelDiscovery();
                    BluetoothDevice device = mBtAdapter.getRemoteDevice(str[1]);
                    mService.connect(device);
                }
            });
            builder.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    };
}
