package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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


public class RankingActivity extends Activity
{
    private static final String request = "3\n";
    private ArrayList<OsobaReducedPlus> usersList;
    private ArrayAdapter<String> usersAdapter;
    private String users;
    EditText rangEditText;
    private int userID;
    SharedPreferences shPref;
    ListView userListView;
    List listRank;
    List listName;
    List listPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        rangEditText = (EditText)findViewById(R.id.currang);
        shPref = getSharedPreferences(Constants.loginpref, Context.MODE_PRIVATE);
        userID = shPref.getInt(Constants.userIDpref, 0);

        listRank = new ArrayList<String>();
        listName = new ArrayList<String>();
        listPoints = new ArrayList<String>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    InetAddress adr = InetAddress.getByName(Constants.address);
                    Socket socket = new Socket(adr, Constants.PORT);
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
                    printWriter.write(request);
                    printWriter.flush();

                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    users = in.readLine();
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    usersList = gson.fromJson(users, new TypeToken<ArrayList<OsobaReducedPlus>>() {}.getType());

                    printWriter.close();
                    socket.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            int pom;
                            int rang = 0;
                            do
                            {
                                pom = usersList.get(rang).getId();
                                rang++;
                            } while (pom != userID);
                            rangEditText.setText(rang + ".");

                            pom = 1;
                            for (Iterator<OsobaReducedPlus> i = usersList.iterator(); i.hasNext(); ) {
                                OsobaReducedPlus o = i.next();
                                listRank.add(pom+".  ");
                                listName.add(o.getUser());
                                listPoints.add(o.getBrPoena()+"");
                                pom++;
                            }
                            final String[] userRank = (String[]) listRank.toArray(new String[listRank.size()]);
                            final String[] userName = (String[]) listName.toArray(new String[listName.size()]);
                            final String[] userPoints = (String[]) listPoints.toArray(new String[listPoints.size()]);

                            usersAdapter = new RankListAdapter(RankingActivity.this, userRank, userName, userPoints);
                            userListView = (ListView) findViewById(R.id.ranking);
                            userListView.setAdapter(usersAdapter);
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
