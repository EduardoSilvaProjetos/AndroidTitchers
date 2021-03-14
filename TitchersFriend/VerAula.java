package pt.ubi.di.pdm.titchersfriend;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class VerAula extends Activity {

    DBHelper dbHelper;
    SQLiteDatabase base;
    TextView data,sumario,notas;
    Button edit;
    String id_at = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veraula);

        //Abrir a base de dados local
        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();

        //recebe o id da atividade por intent
        Intent Cheguei = getIntent();
        id_at = Cheguei.getStringExtra("id");

        data = (TextView)findViewById(R.id.dia_aula);
        sumario = (TextView)findViewById(R.id.inputSumario);
        notas = (TextView)findViewById(R.id.inputNotas);
        edit = (Button)findViewById(R.id.btnEditarAula);

        //Procura pela atividade com o id correspondente e preenche os campos
        Cursor cursor = base.query(dbHelper.TABLE_NAME2,new String[]{"*"},null,null,null,null,null);
        while (cursor.moveToNext()){
            String c1 =cursor.getString(cursor.getColumnIndex(dbHelper.COL1_T2));
            if (c1.equals(id_at)){
                data.setText(cursor.getString(cursor.getColumnIndex(dbHelper.COL3_T2)));

                String aux = cursor.getString(cursor.getColumnIndex(dbHelper.COL2_T2));
                String[] s = aux.split("//");

                sumario.setText(s[0]);
                notas.setText(s[1]);
                break;
            }
        }

        //direciona para a pagina de editar aula
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VerAula.this,EditarAula.class);
                i.putExtra("id",id_at);
                startActivity(i);
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
        Cursor cursor = base.query(dbHelper.TABLE_NAME2,new String[]{"*"},null,null,null,null,null);
        while (cursor.moveToNext()){
            String c1 =cursor.getString(cursor.getColumnIndex(dbHelper.COL1_T2));
            if (c1.equals(id_at)){
                data.setText(cursor.getString(cursor.getColumnIndex(dbHelper.COL3_T2)));

                String aux = cursor.getString(cursor.getColumnIndex(dbHelper.COL2_T2));
                String[] s = aux.split("//");

                sumario.setText(s[0]);
                notas.setText(s[1]);
                break;
            }
        }
    }
}
