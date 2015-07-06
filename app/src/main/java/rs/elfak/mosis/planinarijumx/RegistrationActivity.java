package rs.elfak.mosis.planinarijumx;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.StringTokenizer;


public class RegistrationActivity extends Activity
{
    ImageView viewImage;
    Button b;
    String imageName = null;
    Uri mCapturedImageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        b = (Button)findViewById(R.id.slikaBtn);
        viewImage = (ImageView)findViewById(R.id.viewImage);
    }

    public void onSlikaBtn(View view)
    {
        final CharSequence[] options = { "Slikaj", "Izaberi iz galerije","Otkaži" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Dodaj sliku!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if(options[item].equals("Slikaj"))
                {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "Image File name");
                    mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                    if (intent.resolveActivity(getPackageManager()) != null)
                        startActivityForResult(intent, 1);
                }
                else if(options[item].equals("Izaberi iz galerije"))
                {
                    Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }
                else if(options[item].equals("Otkaži"))
                {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void registrujMe(View view)
    {
        String username,pass,ime,prezime,brtel,imgName;
        imgName = "";
        username = ((EditText) findViewById(R.id.korIme1)).getText().toString();
        pass = ((EditText) findViewById(R.id.lozinka1)).getText().toString();
        ime = ((EditText) findViewById(R.id.ime)).getText().toString();
        prezime = ((EditText) findViewById(R.id.prezime)).getText().toString();
        brtel = ((EditText) findViewById(R.id.brtel)).getText().toString();

        if((username.isEmpty()) || (pass.isEmpty()))
        {
            Toast.makeText(this,getString(R.string.empty_space),Toast.LENGTH_SHORT).show();
            return;
        }


        StringTokenizer stringTokenizer = new StringTokenizer(imageName,"/");

        while(stringTokenizer.hasMoreElements())
        {
            imgName = stringTokenizer.nextToken();
        }


        final File f = new File(imageName);
        NovaOsoba novaOsoba;

        if(f.exists())
            novaOsoba = new NovaOsoba(brtel,ime,pass,prezime,imgName,username, (int) f.length());
        else
            novaOsoba = new NovaOsoba(brtel,ime,pass,prezime,imgName,username,0);

        final String sendBuff = "0\n" + novaOsoba.toString()+"\n";

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress adr = InetAddress.getByName(Constants.address);
                    Socket socket = new Socket(adr, Constants.PORT);
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
                    printWriter.write(sendBuff);
                    printWriter.flush();
                    OutputStream dataOutputStreamput= socket.getOutputStream();
                    if(f.exists()) {

                        FileInputStream fileInputStream = new FileInputStream(f);
                        byte imgBuff[] = new byte[(int) f.length()];
                        fileInputStream.read(imgBuff);
                        dataOutputStreamput.write(imgBuff);
                        dataOutputStreamput.flush();
                    }

                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String prijem = in.readLine();
                    printWriter.close();
                    dataOutputStreamput.close();
                    in.close();
                    socket.close();
                    if(prijem != null) {
                        if (prijem.equals("false"))
                            Toast.makeText(RegistrationActivity.this
                                    , "Neuspelo kreiranje naloga", Toast.LENGTH_SHORT).show();
                        else
                        {
                            SharedPreferences shPref = getSharedPreferences(Constants.loginpref, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shPref.edit();
                            //userID = response;
                            editor.putInt(Constants.userIDpref, 1);
                            editor.commit();
                           // userID = shPref.getInt(Constants.userIDpref, 0);
                            Intent i = new Intent(RegistrationActivity.this, MainActivity.class);
                            startActivity(i);
                        }
                    }

                } catch (IOException e) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            if (requestCode == 1)
            {
                imageName = getRealPathFromUri(mCapturedImageURI);
                Bitmap thumbnail = (BitmapFactory.decodeFile(imageName));
                int nh = (int)(thumbnail.getHeight()*(512.0/thumbnail.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(thumbnail, 512, nh, true);
                viewImage.setImageBitmap(scaled);
            }
            else if(requestCode == 2)
            {
                Uri selectedImage = data.getData();
                //File f = new File("" + selectedImage.getPath());
                //imageName = f.getName();
                String[] filePath = { MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                File f = new File(picturePath);
                imageName = picturePath;
                int nh = (int)(thumbnail.getHeight()*(512.0/thumbnail.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(thumbnail, 512, nh, true);
                viewImage.setImageBitmap(scaled);
            }
        }
    }

    private String getRealPathFromUri(Uri selectedImage) {

        try
        {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        catch (Exception e)
        {
            return selectedImage.getPath();
        }
    }
}













