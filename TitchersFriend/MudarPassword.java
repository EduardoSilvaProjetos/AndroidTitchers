package pt.ubi.di.pdm.titchersfriend;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;


public class MudarPassword extends AppCompatActivity {

    EditText PassAnt, PassNova, RepPassNova;
    Button Submeter,Cancelar;
DBHelper dbHelper;
SQLiteDatabase base;
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
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mudarpass);
        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();
        PassAnt = (EditText) findViewById(R.id.inputPassOld);
        PassNova = (EditText) findViewById(R.id.inputPass);
        RepPassNova = (EditText) findViewById(R.id.inputRepPass);
        Submeter = (Button) findViewById(R.id.btnSubmeterRel);
        Cancelar = (Button) findViewById(R.id.btnCancelarRel);

        Cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Submeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ant, n1, n2;
                ant = PassAnt.getText().toString();
                n1 = PassNova.getText().toString();
                n2 = RepPassNova.getText().toString();
                if(ant.isEmpty() || n1.isEmpty() || n2.isEmpty()){
                    Toast.makeText(MudarPassword.this,"Preencha todos os campos!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!n1.equals(n2)){
                    Toast.makeText(MudarPassword.this,"Novas Passwords Diferem!",Toast.LENGTH_SHORT).show();
                    PassNova.setText("");
                    RepPassNova.setText("");
                    return;
                }
                if(!n1.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$")){
                    PassNova.setText("");
                    RepPassNova.setText("");
                    Toast.makeText(MudarPassword.this,"Password inválida! 8 carateres (1 maiuscula e 1 minuscula).",Toast.LENGTH_SHORT).show();
                    return;
                }
                String x = null;
                Cursor oCursor = base.query(dbHelper.TABLE_NAME7, new String[]{"*"}, null, null, null, null, null, null);
                oCursor.moveToFirst();
                String id =oCursor.getString(oCursor.getColumnIndex(dbHelper.COL1_T7));
                try {
                    x = new Sender(MudarPassword.this,"401","user="+id+"&pwd="+getM5(ant)+"&pwd2="+getM5(n1),null).execute().get();

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        dbHelper.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        base=dbHelper.getWritableDatabase();
    }
}
