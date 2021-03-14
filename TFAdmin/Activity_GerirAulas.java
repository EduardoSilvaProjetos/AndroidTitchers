package pt.ubi.di.pdm.tfadmin;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.function.LongFunction;

public class Activity_GerirAulas extends AppCompatActivity {
    DBHelper dbHelper;
    SQLiteDatabase base;
    LinearLayout oLL;
    Cursor oCursor;
    int id,e_id;
    String i,tk;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geriraulas);

        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();


        SharedPreferences shp = getApplicationContext().getSharedPreferences("important_variables",0);
        id = shp.getInt("id",999);

        Intent Cheguei = getIntent();
        i = Cheguei.getStringExtra("id");
        e_id = Integer.parseInt(i);

        displayAulas();
    }

    protected void onResume() {
        super.onResume();
        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();
        dbHelper.updateEducador(base,id);
        Cursor cursor = base.query(DBHelper.TEDUCADOR,new String[]{DBHelper.COL7_TEDUCADOR},DBHelper.COL1_TEDUCADOR+"=?",new String[]{String.valueOf(e_id)},null,null,null);
        cursor.moveToFirst();
        tk = cursor.getString(0);
        cursor.close();
        int aux = dbHelper.updateAtividade(base,id,e_id,tk);
        oLL.removeAllViews();
        displayAulas();
    }

    @Override
    protected void onPause() {
        super.onPause();
        base.close();
    }


    public void displayAulas() {
        oLL = (LinearLayout) findViewById(R.id.visualizar);
        oLL.removeAllViewsInLayout();
        oCursor = base.query(DBHelper.TATIVIDADE, new String[]{"*"}, null, null, null, null, null, null);
        boolean bCarryOn = oCursor.moveToFirst();
        while (bCarryOn) {
            LinearLayout oLL1 = (LinearLayout) getLayoutInflater().inflate(R.layout.linha_aulas, null);
            oLL1.setId(oCursor.getInt(0) * 10 + 4);

            TextView E1 = (TextView) oLL1.findViewById(R.id.nomeAluno);
            E1.setId(oCursor.getInt(0) * 10 + 3);
            E1.setText(oCursor.getString(2));


            ImageButton oB2 = (ImageButton)oLL1.findViewById(R.id.btnRelatorio);
            oB2.setId(oCursor.getInt(0) * 10 +2);
            oB2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = (v.getId())/10;
                    Intent aula = new Intent(Activity_GerirAulas.this, Activity_GerirRelatorios.class);
                    aula.putExtra("id",String.valueOf(id));
                    aula.putExtra("e_id",String.valueOf(e_id));
                    startActivity(aula);
                }
            });


            ImageButton oB1 = (ImageButton) oLL1.findViewById(R.id.btnVerAula);
            oB1.setId(oCursor.getInt(0) * 10+1);
            oB1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = (v.getId())/10;
                    Intent aula = new Intent(Activity_GerirAulas.this, Activity_VerAula.class);
                    aula.putExtra("id",String.valueOf(id));
                    aula.putExtra("e_id",String.valueOf(e_id));
                    startActivity(aula);
                }
            });

            ImageButton oB3 = (ImageButton) oLL1.findViewById(R.id.btnApagar);
            oB3.setId(oCursor.getInt(0) * 10);
            oB3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("tag",String.valueOf((v.getId())/10));
                   dbHelper.deleteAtividade(id,e_id,(v.getId())/10);
                    LinearLayout oLL1 =(LinearLayout)findViewById((v.getId())+4);
                    ((LinearLayout)oLL1.getParent()).removeView(oLL1);
                }
            });
            oLL.addView(oLL1);
            bCarryOn = oCursor.moveToNext();
        }

    }
}
