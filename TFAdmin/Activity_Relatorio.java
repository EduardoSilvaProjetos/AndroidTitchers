package pt.ubi.di.pdm.tfadmin;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.text.DateFormat;
import java.util.Calendar;

public class Activity_Relatorio extends AppCompatActivity {

    DBHelper dbHelper;
    SQLiteDatabase base;
    CheckBox comer, dormir, Wc, curativo, chorar;
    EditText notas;
    TextView nome;
    Button submeter, cancelar;
    String s = "";
    String VerRel = "";
    String id = "";
    int admin_id,ed_id,at_id;
    int v1 = 0, v2 = 0, v3 = 0, v4 = 0, modo = 0;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);

        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();

        Calendar calendar = Calendar.getInstance();
        String cD = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());

        Intent Cheguei = getIntent();
        id = Cheguei.getStringExtra("id");
        ed_id = Integer.parseInt(Cheguei.getStringExtra("ed_id"));
        at_id = Integer.parseInt(Cheguei.getStringExtra("at_id"));


        Log.d("tag","1");
        SharedPreferences shp = getApplicationContext().getSharedPreferences("important_variables",0);
        admin_id = shp.getInt("id",999);

        nome = findViewById(R.id.titulo_homeeduc);
        submeter = findViewById(R.id.btnSubmeterRel);
        cancelar = findViewById(R.id.btnCancelarRel);
        comer = findViewById(R.id.cboxComer);
        dormir = findViewById(R.id.cboxDormir);
        Wc = findViewById(R.id.cboxWc);
        curativo = findViewById(R.id.cboxMagoar);
        chorar = findViewById(R.id.cboxChorar);
        notas = findViewById(R.id.editTextTextPersonName);

        Cursor cursor = base.query(DBHelper.TRELATORIO,new String[]{"*"},DBHelper.COL0_TRELATORIO+"=?",new String[]{id},null,null,null);
        cursor.moveToFirst();

        nome.setText(cursor.getString(1));

        notas.setText(cursor.getString(4));

        if(cursor.getString(2).equals("1")){
            comer.setChecked(true);
            Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
            comer.setBackground(d1);
        }
        if(cursor.getString(3).equals("1")){
            dormir.setChecked(true);
            Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
            dormir.setBackground(d1);
        }
        if(cursor.getString(5).equals("1")){
            Wc.setChecked(true);
            Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
            Wc.setBackground(d1);
        }
        if(cursor.getString(6).equals("1")){
            curativo.setChecked(true);
            Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
            curativo.setBackground(d1);
        }
        if(cursor.getString(6).equals("2")){
            chorar.setChecked(true);
            Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
           chorar.setBackground(d1);
        }
        if(cursor.getString(6).equals("3")){
            curativo.setChecked(true);
            Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
            curativo.setBackground(d1);
            chorar.setChecked(true);
            chorar.setBackground(d1);
        }

        comer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comer.isChecked()){
                    Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
                    comer.setBackground(d1);
                }else{
                    Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkbox,null);
                    comer.setBackground(d1);
                }
            }
        });

        dormir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dormir.isChecked()){
                    Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
                    dormir.setBackground(d1);
                }else{
                    Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkbox,null);
                    dormir.setBackground(d1);
                }
            }
        });
        Wc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Wc.isChecked()){
                    Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
                    Wc.setBackground(d1);
                }else{
                    Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkbox,null);
                    Wc.setBackground(d1);
                }
            }
        });
        curativo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curativo.isChecked()){
                    Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
                    curativo.setBackground(d1);
                }else{
                    Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkbox,null);
                    curativo.setBackground(d1);
                }
            }
        });
        chorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chorar.isChecked()){
                    Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
                    chorar.setBackground(d1);

                }else{
                    Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkbox,null);
                    chorar.setBackground(d1);
                }
            }
        });


        submeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comer.isChecked())
                    v1 = 1;
                if(dormir.isChecked())
                    v2=1;
                if (Wc.isChecked())
                    v3 = 1;

                if (curativo.isChecked())
                    v4 = 1;
                if (chorar.isChecked())
                    v4 = 2;
                if (chorar.isChecked()&&curativo.isChecked())
                    v4 = 3;
                dbHelper.editRelatorio(admin_id,ed_id,at_id,Integer.parseInt(id),v1,v2,notas.getText().toString(),v3,v4);
                finish();

            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    @Override
    protected void onPause() {
        super.onPause();
        base.close();
    }
    @Override
    public void onResume() {
        super.onResume();
        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();
    }
}
