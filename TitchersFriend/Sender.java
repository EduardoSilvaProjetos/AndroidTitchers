package pt.ubi.di.pdm.titchersfriend;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.util.ArrayList;

/**
 * 1.SEND DATA FROM EDITTEXT OVER THE NETWORK
 * 2.DO IT IN BACKGROUND THREAD
 * 3.READ RESPONSE FROM A SERVER
 */
public class Sender extends AsyncTask<Void,Void,String> {

    Context c;
    String urlAddress = "https://teachersfriend.ddns.net/service.php";
    String resposta;
    String Query, Extra;
    ProgressDialog pd;
    File file = null;

    /*
            1.OUR CONSTRUCTOR
    2.RECEIVE CONTEXT,URL ADDRESS AND EDITTEXTS FROM OUR MAINACTIVITY
    */
    public Sender(Context c, String q, String ext, String filePath) {
        this.c = c;
        Query = q;
        Extra = ext;
        if(filePath!=null) {
            file = new File(filePath);
        }
    }

    /*
   1.SHOW PROGRESS DIALOG WHILE DOWNLOADING DATA
    */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pd = new ProgressDialog(c);
        pd.show();
    }

    /*
    1.WHERE WE SEND DATA TO NETWORK
    2.RETURNS FOR US A STRING
     */
    @Override
    protected String doInBackground(Void... params) {
        try {
            return this.send();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
  1. CALLED WHEN JOB IS OVER
  2. WE DISMISS OUR PD
  3.RECEIVE A STRING FROM DOINBACKGROUND
   */
    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if (response != null) {
            //SUCCESS
            resposta = response;
            //Toast.makeText(c, response, Toast.LENGTH_LONG).show();
        } else {
            //NO SUCCESS
            Toast.makeText(c, "Unsuccessful " + response, Toast.LENGTH_LONG).show();
        }
    }

    /*
    SEND DATA OVER THE NETWORK
    RECEIVE AND RETURN A RESPONSE
     */
    private String send() throws IOException {
        MultipartUtility multipart = null;
        multipart = new MultipartUtility();
        int c =0;
        String aux="";
        ArrayList<String> col = new ArrayList<>();
        ArrayList<String> ex = new ArrayList<>();
        while( c <Extra.length()) {
            if (!(Extra.charAt(c) == '&' || Extra.charAt(c) == '=')) {
                aux = aux + Extra.charAt(c);
            }
            if (Extra.charAt(c) == '=') {
                col.add(aux);
                aux = "";
            }
            if (Extra.charAt(c) == '&') {
                ex.add(aux);
                aux = "";
            }
            if (c == Extra.length() - 1) {
                ex.add(aux);

            }
            c++;
        }
        multipart.addFormField("q", Query);
        for(int i = 0; i <col.size();i++){
            multipart.addFormField(col.get(i) + "", ex.get(i) + "");
        }
        if(file != null){
            multipart.addFilePart("f", file);
            Log.d("FILE",file.getName());
        }else{
            Log.d("FILE","nulo");
        }
        pd.dismiss();
        return multipart.finish(); // response from server.
    }
}

