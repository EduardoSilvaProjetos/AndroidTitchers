package pt.ubi.di.pdm.tfadmin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_GerirRelatorios extends AppCompatActivity {

    Cursor oCursor;
    LinearLayout oLL;
    DBHelper dbHelper;
    SQLiteDatabase base;
    int id,at_id,e_id;
    String i,tk,i2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorios);

        Log.d("tag","1");

        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();

        Log.d("tag","1");
        SharedPreferences shp = getApplicationContext().getSharedPreferences("important_variables",0);
        id = shp.getInt("id",999);

        Log.d("tag","1");
        Intent Cheguei = getIntent();
        i = Cheguei.getStringExtra("id");
        at_id = Integer.parseInt(i);

        Log.d("tag","1");
        i2 = Cheguei.getStringExtra("e_id");
        e_id = Integer.parseInt(i2);
        //displayRelatorios();

    }

    protected void onResume() {
        super.onResume();
        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();
        Cursor cursor = base.query(DBHelper.TEDUCADOR,new String[]{DBHelper.COL7_TEDUCADOR},DBHelper.COL1_TEDUCADOR+"=?",new String[]{String.valueOf(e_id)},null,null,null);
        cursor.moveToFirst();
        tk = cursor.getString(0);
        cursor.close();
        int aux = dbHelper.updateRelatorio(base,id,e_id,tk,at_id);

        displayRelatorios();
    }

    @Override
    protected void onPause() {
        super.onPause();
        base.close();
    }

    public void displayRelatorios() {
        oLL = (LinearLayout) findViewById(R.id.visualizar);
        oLL.removeAllViews();
        oCursor = base.query(DBHelper.TRELATORIO, new String[]{"*"}, null, null, null, null, null, null);
        boolean bCarryOn = oCursor.moveToFirst();
        while (bCarryOn) {
            LinearLayout oLL1 = (LinearLayout) getLayoutInflater().inflate(R.layout.linha_visualizar, null);
            oLL1.setId(oCursor.getInt(0) * 10 + 3);

            TextView E1 = (TextView) oLL1.findViewById(R.id.nomeAluno);
            E1.setId(oCursor.getInt(0) * 10 +2);
            E1.setText(oCursor.getString(1));

            ImageButton B1 = (ImageButton)oLL1.findViewById(R.id.btnVerAluno);
            B1.setId(oCursor.getInt(0)*10+1);
            B1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent relatorio = new Intent(Activity_GerirRelatorios.this,Activity_Relatorio.class);
                    relatorio.putExtra("id",String.valueOf((v.getId())/10));
                    relatorio.putExtra("at_id",String.valueOf(at_id));
                    relatorio.putExtra("ed_id",String.valueOf(e_id));
                    startActivity(relatorio);
                }
            });

            ImageButton B2 = (ImageButton)oLL1.findViewById(R.id.btnApagar);
            B2.setId(oCursor.getInt(0)*10);
            B2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbHelper.deleteRelatorio(id,e_id,(v.getId())/10,at_id);
                    LinearLayout oLL1 =(LinearLayout)findViewById((v.getId())+3);
                    ((LinearLayout)oLL1.getParent()).removeView(oLL1);
                }
            });


            oLL.addView(oLL1);
            bCarryOn = oCursor.moveToNext();
        }

    }


}
