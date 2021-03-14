package pt.ubi.di.pdm.titchersfriend;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GerirAulas extends AppCompatActivity {

    Button sumario;
    DBHelper dbHelper;
    SQLiteDatabase base;
    LinearLayout oLL;
    Cursor oCursor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geriraulas);

        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();

        sumario = (Button)findViewById(R.id.btnAddAula);
        sumario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GerirAulas.this,Sumarios.class);
                startActivity(i);
            }
        });

        displayAulas();

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
        oLL.removeAllViews();
        displayAulas();
    }

    public void displayAulas() {
        oLL = (LinearLayout) findViewById(R.id.visualizar);
        oCursor = base.query(dbHelper.TABLE_NAME2, new String[]{"*"}, null, null, null, null, null, null);

        boolean bCarryOn = oCursor.moveToFirst();
        while (bCarryOn) {
            LinearLayout oLL1 = (LinearLayout) getLayoutInflater().inflate(R.layout.linha_aula, null);
            oLL1.setId(oCursor.getInt(0) * 10 + 2);

            TextView E1 = (TextView) oLL1.findViewById(R.id.nomeAluno);
            E1.setId(oCursor.getInt(0) * 10 + 1);
            E1.setText(oCursor.getString(2));


            ImageButton oB1 = (ImageButton) oLL1.findViewById(R.id.btnVerAula);
            oB1.setId(oCursor.getInt(0) * 10);
            oB1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = (v.getId())/10;
                    Intent aula = new Intent(GerirAulas.this, VerAula.class);
                    aula.putExtra("id",String.valueOf(id));
                    startActivity(aula);
                }
            });
            oLL.addView(oLL1);
            bCarryOn = oCursor.moveToNext();
        }

    }
}
