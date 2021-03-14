package pt.ubi.di.pdm.tfadmin;

import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;

public class Activity_AddAlergiaAluno extends AppCompatActivity {

    LinearLayout oLL;
    Cursor oCursor;
    DBHelper oDBH;
    SQLiteDatabase oSQLDB;
    Button sub, canc;
    DBHelper dbHelper;
    EditText addA;
    ArrayList<String> subm = new ArrayList<String>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addalergiaaluno);


        dbHelper = new DBHelper(this);
        oDBH = new DBHelper(this);
        oSQLDB = oDBH.getWritableDatabase();
        sub = (Button)findViewById(R.id.btnSubmeterAlergias);
        canc = (Button)findViewById(R.id.btnCancelarAlergias);
        addA= (EditText)findViewById(R.id.addAlergia);
        displayAlunos();

        canc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED,null);
                finish();
            }
        });


        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(subm.size()<1){
                    Toast.makeText(Activity_AddAlergiaAluno.this,"Escolha alguma alergia ou cancele",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",subm.toString());
                Log.d("tag",subm.get(0));
                setResult(Activity.RESULT_OK,returnIntent);
                Toast.makeText(Activity_AddAlergiaAluno.this,"Adicionado com sucesso",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    protected void onResume() {
        super.onResume();



        Cursor cursor = oSQLDB.query(DBHelper.TEDUCADOR,new String[]{DBHelper.COL7_TEDUCADOR},DBHelper.COL1_TEDUCADOR+"=?",new String[]{String.valueOf(getIntent().getStringExtra("ed_id"))},null,null,null);
        cursor.moveToFirst();
        String tk = cursor.getString(0);
        cursor.close();

        SharedPreferences shp = getApplicationContext().getSharedPreferences("important_variables",0);
        int id = shp.getInt("id",999);

        Log.d("TOKEN",tk);
        int aux = dbHelper.updateAlergia(oSQLDB,id,Integer.parseInt(getIntent().getStringExtra("ed_id")),tk);

        displayAlunos();
    }

    public void displayAlunos() {

        oLL = (LinearLayout) findViewById(R.id.listaAlergias);
        oCursor = oSQLDB.query(oDBH.TALERGIA, new String[]{"*"}, null, null, null, null, null, null);

        boolean bCarryOn = oCursor.moveToFirst();
        while (bCarryOn) {
            LinearLayout oLL1 = (LinearLayout) getLayoutInflater().inflate(R.layout.linha_faltas, null);
            oLL1.setId(oCursor.getInt(1) * 10 + 2);

            TextView E1 = (TextView) oLL1.findViewById(R.id.nomeAluno);
            E1.setId(oCursor.getInt(1) * 10 + 1);
            E1.setText(oCursor.getString(2));

            final CheckBox T2 = (CheckBox) oLL1.findViewById(R.id.checkBox);
            T2.setId(oCursor.getInt(1) * 10);
            T2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(T2.isChecked()){
                        Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
                        T2.setBackground(d1);
                        String id = String.valueOf((v.getId())/10);
                        Log.d("tag",id);
                        subm.add(id);

                    }else{
                        Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkbox,null);
                        T2.setBackground(d1);
                        String id = String.valueOf((v.getId())/10);
                        subm.remove(id);

                    }
                }
            });
            oLL.addView(oLL1);
            bCarryOn = oCursor.moveToNext();
        }

    }

}
