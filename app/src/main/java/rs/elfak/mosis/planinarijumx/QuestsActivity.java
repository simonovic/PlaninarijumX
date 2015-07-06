package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class QuestsActivity extends Activity
{
    ListView questList;
    QuestListAdapter adapter;
    private int userId;
    private String response;
    ArrayList<Quest> quests;
    private List<String> nameList;
    private List<String> nameStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quests);

        nameList = new ArrayList<String>();
        nameStatus = new ArrayList<String>();

        Bundle extras = getIntent().getExtras();
        userId = extras.getInt("userID");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    String request = "2\n"+userId+"\n";
                    InetAddress adr = InetAddress.getByName(Constants.address);
                    Socket socket = new Socket(adr, Constants.PORT);
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
                    printWriter.write(request);
                    printWriter.flush();

                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    response = in.readLine();
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    quests = gson.fromJson(response, new TypeToken<ArrayList<Quest>>() {}.getType());

                    printWriter.close();
                    socket.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            int br = 1;
                            for (Iterator<Quest> i = quests.iterator(); i.hasNext(); ) {
                                Quest q = i.next();
                                nameList.add(br+".  "+q.getIme());
                                nameStatus.add("");
                                br++;
                            }
                            final String[] name = nameList.toArray(new String[nameList.size()]);
                            final String[] status = nameStatus.toArray(new String[nameStatus.size()]);

                            adapter = new QuestListAdapter(QuestsActivity.this, name, status);
                            questList = (ListView) findViewById(R.id.list);
                            questList.setAdapter(adapter);

                            questList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Toast.makeText(getApplicationContext(), name[position], Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        LogActivity.trenutnaAktivnost = this;
    }
}
