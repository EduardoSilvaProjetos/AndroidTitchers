package pt.ubi.di.pdm.tfadmin;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_PerfEduc extends AppCompatActivity {

    DBHelper dbHelper;
    SQLiteDatabase user_db;
    TextView nome,idade,sexo,morada,contacto;
    String id_educ;
    Button editar_educ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfeduc);
        dbHelper = new DBHelper(this);
        user_db = dbHelper.getWritableDatabase();

        Intent intent_with_id = getIntent();
        id_educ = intent_with_id.getStringExtra("id");
        Log.v("DEBUG", "id obtido: " + id_educ);

        nome = (TextView)findViewById(R.id.txtNome);
        idade = (TextView)findViewById(R.id.txtIdade);
        sexo = (TextView)findViewById(R.id.txtSexo);
        morada = (TextView)findViewById(R.id.txtMorada);
        contacto = (TextView)findViewById(R.id.txtContacto);

        Cursor cursor = user_db.query(DBHelper.TEDUCADOR,new String[]{"*"},null,null,null,null,null);
        while (cursor.moveToNext()){
            String c1 =cursor.getString(cursor.getColumnIndex(DBHelper.COL1_TEDUCADOR));
            if (c1.equals(id_educ)){
                nome.setText("Nome: " + cursor.getString(cursor.getColumnIndex(DBHelper.COL2_TEDUCADOR)));
                idade.setText("Idade: " + cursor.getString(cursor.getColumnIndex(DBHelper.COL3_TEDUCADOR)));

                if(cursor.getString(cursor.getColumnIndex(DBHelper.COL5_TEDUCADOR)).equals("1"))
                    sexo.setText("Sexo: Masculino");
                else
                    sexo.setText("Sexo: Feminino");

                morada.setText("morada: "+cursor.getString(cursor.getColumnIndex(DBHelper.COL4_TEDUCADOR)));
                contacto.setText("contacto: "+cursor.getString(cursor.getColumnIndex(DBHelper.COL6_TEDUCADOR)));
                break;
            }
        }

        editar_educ = (Button) findViewById(R.id.btnEditarEduc);
        editar_educ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editar_educ = new Intent(Activity_PerfEduc.this, Activity_EditarEducadores.class);
                editar_educ.putExtra("id", id_educ);
                startActivity(editar_educ);
            }
        });
    }
}