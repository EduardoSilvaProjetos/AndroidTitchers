package pt.ubi.di.pdm.titchersfriend;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Sumarios extends AppCompatActivity {

    DBHelper dbHelper;
    SQLiteDatabase base;
    EditText Esumario,Enotas;
    Button btnSubmeter,btnCancelar;

    String date;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sumarios);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String monthS, dayS, yearS;
        if(month<10)
            monthS = "0"+(month+1);
        else
            monthS = ""+(month+1);
        yearS = ""+(year%100);
        if(day<10)
            dayS = "0"+day;
        else
            dayS = ""+day;
        date = dayS+"/"+monthS+"/"+yearS;

        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();

        Esumario = (EditText)findViewById(R.id.inputSumario);
        Enotas = (EditText)findViewById(R.id.inputNotas);

        btnCancelar = (Button)findViewById(R.id.btnCancelarRel);
        btnSubmeter = (Button)findViewById(R.id.btnSubmeterRel);
        boolean check = false;
        Cursor cursor =base.query(dbHelper.TABLE_NAME2,new String[]{"*"},null,null,null,null,null);
        while (cursor.moveToNext()){

            String c1 =cursor.getString(cursor.getColumnIndex(dbHelper.COL3_T2));
            if (c1.contains(date)){
                check = true;
                break;
            }
        }
        if (check){
            Toast.makeText(Sumarios.this,"Já submeteu um sumário hoje",Toast.LENGTH_SHORT).show();
            finish();
        }
        btnSubmeter.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                ContentValues oCV = new ContentValues();
                String Totalsum = "";
                String sum = Esumario.getText().toString();
                String notas = Enotas.getText().toString();

                if(sum.equals("")){
                    Toast.makeText(Sumarios.this,"Não preencheu o sumárario",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(sum.contains(",") || notas.contains(",")){
                    Toast.makeText(Sumarios.this,"Não pode meter virgulas no sumário nem nas notas",Toast.LENGTH_SHORT).show();
                    return;
                }

                Totalsum = "Sumario:"+sum+"//Notas:"+notas;

                oCV.put(dbHelper.COL2_T2,Totalsum);
                oCV.put(dbHelper.COL3_T2,date);
                base.insert(dbHelper.TABLE_NAME2,null,oCV);
                Toast.makeText(Sumarios.this,"Submetido!",Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
