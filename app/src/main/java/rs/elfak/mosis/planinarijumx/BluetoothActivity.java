package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class BluetoothActivity extends Activity
{
    private static final String NAME = "PlaninarijumX";
    private static final UUID MY_UUID = UUID.fromString("118a2516-eeb1-4eb4-8c33-7fdc7e4afe2f");
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    public int state = STATE_NONE;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    BluetoothAdapter bluetoothAdapter;
    ArrayAdapter<String> detDevicesArray;
    ListView detDevices;
    ProgressBar progress;
    Button detectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_bluetooth);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        detDevicesArray = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        detDevices = (ListView)findViewById(R.id.listView);
        detDevices.setAdapter(detDevicesArray);
        detDevices.setOnItemClickListener(DeviceClickListener);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        detectBtn = (Button)findViewById(R.id.detectBtn);
        progress = (ProgressBar)findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if(!bluetoothAdapter.isEnabled())
        {
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i, 1);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Bluetooth je uključen!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(bluetoothAdapter != null)
            bluetoothAdapter.cancelDiscovery();
        unregisterReceiver(mReceiver);
        this.stop();
    }

    private AdapterView.OnItemClickListener DeviceClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3)
        {
            bluetoothAdapter.cancelDiscovery();
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
            connect(device);
            /*if (state != STATE_CONNECTED) {
                Toast.makeText(getApplicationContext(), "Odbijeno uparivanje uređaja!", Toast.LENGTH_SHORT).show();
                return;
            }*/
            byte[] send = address.getBytes();
            write(send);
        }
    };

    private void ensureDiscoverable()
    {
        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
        {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    public void onDetectBtn(View view)
    {
        // ovde samo za probu
        ensureDiscoverable();

        progress.setVisibility(View.VISIBLE);
        detectBtn.setClickable(false);

        if(detDevicesArray.getCount() != 0)
        {
            detDevicesArray.clear();
            detDevicesArray.notifyDataSetChanged();
        }

        if(bluetoothAdapter.isDiscovering())
            bluetoothAdapter.cancelDiscovery();

        bluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                detDevicesArray.add(device.getName() + "\n" + device.getAddress());
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                progress.setVisibility(View.GONE);

                if(detDevicesArray.getCount() == 0)
                    detDevicesArray.add("Nijedan uređaj nije pronađen" + "\n" + "Pokušajte ponovo");

                detectBtn.setClickable(true);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK)
        {
            if(requestCode == 1)
            {
                Toast.makeText(getApplicationContext(), "Uspelo uključivanje Bluetooth-a!", Toast.LENGTH_LONG).show();
            }
        }
        else if(resultCode == RESULT_CANCELED)
            Toast.makeText(getApplicationContext(), "Neuspelo uključivanje Bluetooth-a!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed()
    {
        if(bluetoothAdapter.isEnabled())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Isključiti Bluetooth!");
            builder.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    bluetoothAdapter.disable();
                    finish();
                }
            });
            builder.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public synchronized void start()
    {
        if(connectThread != null)
        {
            connectThread.cancel();
            connectThread = null;
        }

        if(connectedThread != null)
        {
            connectedThread.cancel();
            connectedThread = null;
        }

        state = STATE_LISTEN;

        if(acceptThread == null)
        {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
    }

    public synchronized void connect(BluetoothDevice device)
    {
        if(state == STATE_CONNECTING)
            if(connectThread != null)
            {
                connectThread.cancel();
                connectThread = null;
            }

        if(connectedThread != null)
        {
            connectedThread.cancel();
            connectedThread = null;
        }

        connectThread = new ConnectThread(device);
        connectThread.start();
        state = STATE_CONNECTING;
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device)
    {
        if(connectThread != null)
        {
            connectThread.cancel();
            connectThread = null;
        }

        if(connectedThread != null)
        {
            connectedThread.cancel();
            connectedThread = null;
        }

        if(acceptThread != null)
        {
            acceptThread.cancel();
            acceptThread = null;
        }

        connectedThread = new ConnectedThread(socket);
        connectedThread.start();

        state = STATE_CONNECTED;
    }

    public synchronized void stop()
    {
        if(connectThread != null)
        {
            connectThread.cancel();
            connectThread = null;
        }

        if(connectedThread != null)
        {
            connectedThread.cancel();
            connectedThread = null;
        }

        if(acceptThread != null)
        {
            acceptThread.cancel();
            acceptThread = null;
        }

        state = STATE_NONE;
    }

    public void write(byte[] out)
    {
        ConnectedThread r;
        synchronized (this) {
            if (state != STATE_CONNECTED) return;
            r = connectedThread;
        }
        r.write(out);
    }

    private void connectionFailed()
    {


        // Start the service over to restart listening mode
        BluetoothActivity.this.start();
    }

    private void connectionLost()
    {
        //treba poslati poruku

        BluetoothActivity.this.start();
    }

    private class AcceptThread extends Thread
    {
        private final BluetoothServerSocket serverSocket;

        public  AcceptThread()
        {
            BluetoothServerSocket tmp = null;
            try
            {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            }
            catch (IOException e) { Log.e("PlaninarijumX", "Neuspelo kreiranje server socket-a!", e); }
            serverSocket = tmp;
        }

        public void run()
        {
            BluetoothSocket socket;
            while(state != STATE_CONNECTED)
            {
                try
                {
                    socket = serverSocket.accept();
                }
                catch (IOException e) { Log.e("PlaninarijumX", "Neuspelo osluškivanje konekcija!", e); break; }

                if(socket != null)
                {
                    synchronized (this)
                    {
                        switch(state)
                        {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                try
                                {
                                    socket.close();
                                }
                                catch (IOException e) { Log.e("PlaninarijumX", "Nije uspelo zatvaranje "); break; }
                        }
                    }
                }
            }
        }

        public void cancel()
        {
            try
            {
                serverSocket.close();
            }
            catch(IOException e) { Log.e("PlaninarijumX", "Neuspelo zatvaranje server socket-a!", e); }
        }
    }

    private class ConnectThread extends Thread
    {
        private final BluetoothSocket socket;
        private final BluetoothDevice device;

        public ConnectThread(BluetoothDevice dev)
        {
            device = dev;
            BluetoothSocket tmp = null;

            try
            {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            }
            catch(IOException e) { Log.e("PlaninarijumX", "Neuspelo dobijanje socket-a!", e); }
            socket = tmp;
        }

        public void run()
        {
            bluetoothAdapter.cancelDiscovery();

            try
            {
                socket.connect();
            }
            catch(IOException e)
            {
                try
                {
                    socket.close();
                }
                catch(IOException e1) { Log.e("PlaninarijumX", "Neuspelo zatvaranje socket-a u catch bloku!", e1); }
                connectionFailed();
                return;
            }

            synchronized (this)
            {
                connectThread = null;
            }

            connected(socket, device);
        }

        public void cancel()
        {
            try
            {
                socket.close();
            }
            catch(IOException e) { Log.e("PlaninarijumX", "Neuspelo zatvaranje socket-a u catch bloku!", e); }
        }
    }

    private class ConnectedThread extends Thread
    {
        private final BluetoothSocket socket;
        private final InputStream inStream;
        private final OutputStream outStream;

        public ConnectedThread(BluetoothSocket socket1)
        {
            socket = socket1;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch(IOException e) { Log.e("PlaninarijumX", "Neuspelo kreiranje temp socket-a!", e); }

            inStream = tmpIn;
            outStream = tmpOut;
        }

        public void run()
        {
            byte[] buffer = new byte[1024];
            int bytes;

            while(true)
            {
                try
                {
                    bytes = inStream.read(buffer);
                    String tmp = new String(buffer, "UTF-8");
                    Toast.makeText(getApplicationContext(), tmp, Toast.LENGTH_LONG).show();
                }
                catch(IOException e)
                {
                    Log.e("PlaninarijumX", "Diskonektovani!", e);
                    connectionLost();
                    BluetoothActivity.this.start();
                    break;
                }
            }
        }

        public void write(byte[] buffer)
        {
            try
            {
                outStream.write(buffer);
                //saljes preko handler-a poruku ka UI activity-ju
            }
            catch(IOException e) { Log.e("PlaninarijumX", "Greška prilikom slanja poruke!", e); }
        }

        public void cancel()
        {
            try
            {
                socket.close();
            }
            catch(IOException e) { Log.e("PlaninarijumX", "Neuspelo zatvaranje socket-a u catch bloku!", e); }
        }
    }
}


