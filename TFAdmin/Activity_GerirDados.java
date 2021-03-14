package pt.ubi.di.pdm.tfadmin;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutionException;

public class Activity_GerirDados extends AppCompatActivity {

    DBHelper dbHelper;
    SQLiteDatabase base;
    LinearLayout oLL;
    Cursor oCursor;
    String x;
    int u,aux;
    String us;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerirdados);

        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();

        SharedPreferences shp = getApplicationContext().getSharedPreferences("important_variables",0);
        u = shp.getInt("id",999);

    }

    protected void onResume() {
        super.onResume();

        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();

        aux = dbHelper.updateEducador(base,u);
        if ( aux == 0 || aux == -1){
            Toast.makeText(Activity_GerirDados.this,"Erro",Toast.LENGTH_SHORT).show();
        }
        displayEducadoras();

    }

    @Override
    protected void onPause() {
        super.onPause();
        base.close();
    }

    public void displayEducadoras() {
        oLL = (LinearLayout) findViewById(R.id.visualizar);
        oLL.removeAllViewsInLayout();
        oCursor = base.query(DBHelper.TEDUCADOR, new String[]{"*"}, null, null, null, null, null, null);

        boolean bCarryOn = oCursor.moveToFirst();
        while (bCarryOn) {
            LinearLayout oLL1 = (LinearLayout) getLayoutInflater().inflate(R.layout.linha_visualizar, null);
            oLL1.setId(oCursor.getInt(0) * 10 + 3);

            TextView T1 = (TextView) oLL1.findViewById(R.id.nomeAluno);
            T1.setId(oCursor.getInt(0) * 10 + 2);
            T1.setText(oCursor.getString(1));

            TextView T2 = (TextView) oLL1.findViewById(R.id.idAluno);
            T2.setId(oCursor.getInt(0) * 10 + 1);
            T2.setText(oCursor.getString(0));

            ImageButton B1 = (ImageButton)oLL1.findViewById(R.id.btnVerAluno);
            B1.setId(oCursor.getInt(0) * 10);
            B1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Activity_GerirDados.this,Activity_GerirTurma.class);
                    i.putExtra("id",String.valueOf((v.getId())/10));
                    startActivity(i);
                }
            });

            oLL.addView(oLL1);
            bCarryOn = oCursor.moveToNext();
        }

    }

}
