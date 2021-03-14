package pt.ubi.di.pdm.titchersfriend;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditarAula extends Activity {

    DBHelper dbHelper;
    SQLiteDatabase base;
    EditText sumario, notas;
    TextView data;
    Button submeter, cancelar;
    String id_at = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editaraula);

        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();

        Intent Cheguei = getIntent();
        id_at = Cheguei.getStringExtra("id");

        submeter = (Button) findViewById(R.id.btnSubmeterRel);
        cancelar = (Button) findViewById(R.id.btnCancelarRel);

        data = (TextView) findViewById(R.id.dia_aula);
        sumario = (EditText) findViewById(R.id.inputSumario);
        notas = (EditText) findViewById(R.id.inputNotas);

        Cursor cursor = base.query(dbHelper.TABLE_NAME2, new String[]{"*"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String c1 = cursor.getString(cursor.getColumnIndex(dbHelper.COL1_T2));
            if (c1.equals(id_at)) {
                data.setText(cursor.getString(cursor.getColumnIndex(dbHelper.COL3_T2)));

                String aux = cursor.getString(cursor.getColumnIndex(dbHelper.COL2_T2));
                String[] s = aux.split("//");

                s[0] = s[0].replace("Sumario:","");
                s[1] = s[1].replace("Notas:","");

                sumario.setText(s[0]);
                notas.setText(s[1]);
                break;
            }

            submeter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContentValues oCV = new ContentValues();

                    int x = sumario.getText().toString().indexOf("");

                    oCV.put(dbHelper.COL1_T2, id_at);
                    oCV.put(dbHelper.COL3_T2, data.getText().toString());
                    oCV.put(dbHelper.COL2_T2,"Sumario:"+ sumario.getText().toString() + "//Notas:" + notas.getText().toString());


                    base.update(dbHelper.TABLE_NAME2, oCV, dbHelper.COL1_T2 + "=?", new String[]{String.valueOf(id_at)});
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
