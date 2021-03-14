package pt.ubi.di.pdm.tfadmin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class Activity_Login extends AppCompatActivity {
    Button btnLogin;
    EditText inputpass,inputUser;
    DBHelper dbHelper;
    SQLiteDatabase global_db;

    public static String getM5(String input) {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext;
        }   // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        inputpass = (EditText) findViewById(R.id.inputPass);
        inputUser = (EditText) findViewById(R.id.inputUser);

        // Intent iCameFromActivity1 = getIntent() ;
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = "false";
                String us = inputUser.getText().toString();
                String pass = inputpass.getText().toString();
                String enc = getM5(pass);

                Log.v("DEBUG","got the creds");

                try {
                    Log.v("DEBUG", "before sender");
                    x = new Sender(Activity_Login.this, "99", "u=" + us + "&p=" + enc, null).execute().get();
                    Log.v("DEBUG", "got sender: " + x + "<-- that was it");
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                JSONObject reader;
                Boolean s = false;

                try {
                    if (x == null || x == "") {
                        Log.v("DEBUG", "returned null");
                        return;
                    }
                    reader = new JSONObject(x);
                    if(reader.has("success")){
                        s = reader.getBoolean("success");
                        Log.v("DEBUG", "crashes here, at success: " + s);
                    } else{
                        Toast.makeText(Activity_Login.this, "Credenciais inválidas, tente novamente", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(Activity_Login.this, "Credenciais inválidas, tente novamente", Toast.LENGTH_SHORT).show();
                    inputpass.setText("");
                    inputUser.setText("");
                    e.printStackTrace();
                    return;
                }

                if (s) {
                    //receber dados
                    try {
                        reader = new JSONObject(x);
                        SharedPreferences oSP = getSharedPreferences("important_variables", 0);
                        SharedPreferences.Editor oEditor = oSP.edit();
                        oEditor.putInt("id", Integer.parseInt(reader.getString("id")));
                        oEditor.apply();
                        Log.v("DEBUG", "id set in Login: " + oSP.getInt("id", 999));
                        //RecebeDados(x, us);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent go_to_admin_homepage = new Intent(Activity_Login.this, Activity_HomePageAdmin.class);
                    startActivity(go_to_admin_homepage);
                } else {
                    JSONObject erro = null;
                    String e = "erro";

                    try {
                        erro = new JSONObject(x);
                    } catch (JSONException json_e) {
                        json_e.printStackTrace();
                    }

                    try {
                        e = erro.getString("error");
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }

                    Toast.makeText(Activity_Login.this, e, Toast.LENGTH_SHORT).show();
                    inputpass.setText("");
                    inputUser.setText("");
                    return;
                }


                /* placeholder code, quando ainda tentava perceber isto
                if(x.contains("true")){
                    Intent A2 = new Intent(LoginActivity.this,HomePageAdmin.class);
                    startActivity(A2) ;
                }

                //if(x.contains("true")){
                    //Intent A2 = new Intent(LoginActivity.this,HomePageEduc.class);
                    //startActivity(A2) ;
                //}

                if(x.contains("false")){
                    Intent A2 = new Intent(LoginActivity.this,LoginActivity.class);
                    finish();
                    startActivity(A2);
                }
                */
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
    }


}
