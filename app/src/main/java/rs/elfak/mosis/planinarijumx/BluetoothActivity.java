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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


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
    int userID;
    SharedPreferences shPref;
    private boolean sender = false;
    private String responseID;
    private String friendsID = "";
    private String friendDeviceName = "";
    private String[] friendsIDs1;
    private ArrayList<String> friendsIDs;
    private String[] str;
    private final String STATE_FRIENDIDS = "friendids";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            friendsID = extras.getString("friendsID");
        }
        else
        {
            friendsID = savedInstanceState.getString(STATE_FRIENDIDS);
        }

        friendsIDs1 = friendsID.split(" ");

        if (friendsID.equals(""))
            friendsIDs = new ArrayList();
        else
            friendsIDs = new ArrayList(Arrays.asList(friendsIDs1));

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
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        friendsID = "";
        int size = friendsIDs.size();
        for (int i=0; i<size; i++)
            if (i == size-1)
                friendsID += friendsIDs.get(i);
            else
                friendsID += friendsIDs.get(i) + " ";
        savedInstanceState.putString(STATE_FRIENDIDS, friendsID);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == -1)

                mService = new BluetoothService(this, mHandler);
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
                        responseID = tmp[1];
                        if (friendsIDs.contains(tmp[1]))
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothActivity.this);
                            builder.setTitle("Prijateljstvo!");
                            builder.setMessage("Već ste prijatelj sa korisnikom " + friendDeviceName);
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                        else
                        {
                            String pom = "request " + deviceName;
                            byte[] pom1 = pom.getBytes();
                            mService.write(pom1);
                        }
                    }
                    else if (tmp[0].equals("request"))
                    {
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
                        RespondeYes(readMessage.substring(12));
                    }
                    else if (tmp[0].equals("responseNo")) {
                        RespondeNo(readMessage.substring(11));
                    }
                    else if (tmp[0].equals("Success"))
                    {
                        friendsIDs.add(tmp[1]);
                        Toast.makeText(BluetoothActivity.this, "Postali ste prijatelji!", Toast.LENGTH_LONG).show();
                    }
                    else if (tmp[0].equals("Failed"))
                    {
                        Toast.makeText(BluetoothActivity.this, "Spijateljavanje sa " + tmp[1] + " nije uspelo!", Toast.LENGTH_LONG).show();
                    }
                    break;
                case 5:
                    Toast.makeText(BluetoothActivity.this, "Konekcija je neuspela, pokušajte ponovo!", Toast.LENGTH_LONG).show();
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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            String request = "8\n" + userID + "\n" + responseID + "\n";
                            InetAddress adr = InetAddress.getByName(Constants.address);
                            Socket socket = new Socket(adr, Constants.PORT);
                            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                            printWriter.write(request);
                            printWriter.flush();

                            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            String serverResponse = in.readLine();

                            printWriter.close();
                            socket.close();

                            if (serverResponse.equals("false")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Greška prilikom sprijateljavanja, pokušajte ponovo!", Toast.LENGTH_LONG).show();
                                        String m = "Failed " + deviceName;
                                        byte[] m1 = m.getBytes();
                                        mService.write(m1);
                                        mService.start();
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "Postali ste prijatelji!", Toast.LENGTH_LONG).show();
                                friendsIDs.add(responseID);
                                String m = "Success " + userID;
                                byte[] m1 = m.getBytes();
                                mService.write(m1);
                                mService.start();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        AlertDialog dialog = builder2.create();
        dialog.show();
    }

    public void onDetectBtn(View view)
    {
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
            Toast.makeText(getApplicationContext(), "Morate uključiti Bluetooth!", Toast.LENGTH_LONG).show();

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
            friendDeviceName = str[0] + " [" + str[1] + "]";

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bluetooth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.discoverable:
                if (mBtAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 360);
                    startActivity(discoverableIntent);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
