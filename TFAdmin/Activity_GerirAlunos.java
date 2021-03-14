package pt.ubi.di.pdm.tfadmin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_GerirAlunos extends AppCompatActivity {

    DBHelper dbHelper;
    SQLiteDatabase base;
    LinearLayout oLL;
    Cursor oCursor;
    String tk,i;
    int e_id,id;
    Button add;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geriralunos);

        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();

        Intent Cheguei = getIntent();

        i = Cheguei.getStringExtra("id");

        e_id = Integer.parseInt(i);

        SharedPreferences shp = getApplicationContext().getSharedPreferences("important_variables",0);
        id = shp.getInt("id",999);

        add = (Button)findViewById(R.id.btnAddAluno);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addA = new Intent(Activity_GerirAlunos.this,Activity_AddAluno.class);
                addA.putExtra("ed_id",i);
                startActivity(addA);
            }
        });

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
        dbHelper.updateEducando(base,id,e_id,tk);
        displayAlunos();
    }

    @Override
    protected void onPause() {
        super.onPause();
        base.close();
    }

    public void displayAlunos() {
        oLL = (LinearLayout) findViewById(R.id.visualizar);
        oLL.removeAllViewsInLayout();
        oCursor = base.query(DBHelper.TEDUCANDO, new String[]{"*"}, null, null, null, null, null, null);

        boolean bCarryOn = oCursor.moveToFirst();
        while (bCarryOn) {
            LinearLayout oLL1 = (LinearLayout) getLayoutInflater().inflate(R.layout.linha_visualizar, null);
            oLL1.setId(oCursor.getInt(0) * 10 + 4);

            TextView T1 = (TextView) oLL1.findViewById(R.id.nomeAluno);
            T1.setId(oCursor.getInt(0) * 10 + 3);
            T1.setText(oCursor.getString(1));

            TextView T2 = (TextView) oLL1.findViewById(R.id.idAluno);
            T2.setId(oCursor.getInt(0) * 10 + 2);
            T2.setText(oCursor.getString(0));

            ImageButton B1 = (ImageButton)oLL1.findViewById(R.id.btnVerAluno);
            B1.setId(oCursor.getInt(0) * 10+1);
            B1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent ia = new Intent(Activity_GerirAlunos.this,Activity_PerfAluno.class);
                    ia.putExtra("id",String.valueOf((v.getId())/10));
                    ia.putExtra("id_educadora",i);
                    startActivity(ia);
                }
            });

            ImageButton B2 = (ImageButton)oLL1.findViewById(R.id.btnApagar);
            B2.setId(oCursor.getInt(0) * 10);
            B2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbHelper.deleteEducando(id,e_id,(v.getId())/10);
                    LinearLayout oLL1 =(LinearLayout)findViewById((v.getId())+4);
                    ((LinearLayout)oLL1.getParent()).removeView(oLL1);
                }
            });

            oLL.addView(oLL1);
            bCarryOn = oCursor.moveToNext();
        }

    }
}
