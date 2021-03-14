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

public class Activity_PerfAdmin extends AppCompatActivity {

    DBHelper dbHelper;
    SQLiteDatabase user_db;
    TextView nome, idade, sexo, morada, contacto;
    String id_admin;

    Button editar_admin;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfeduc);
        dbHelper = new DBHelper(this);
        user_db = dbHelper.getWritableDatabase();

        Intent intent_with_id = getIntent();
        id_admin = intent_with_id.getStringExtra("id");
        Log.v("DEBUG", "id obtido: " + id_admin);

        nome = findViewById(R.id.txtNome);
        idade = findViewById(R.id.txtIdade);
        sexo = findViewById(R.id.txtSexo);
        morada = findViewById(R.id.txtMorada);
        contacto = findViewById(R.id.txtContacto);

        Cursor cursor = user_db.query(DBHelper.TADMIN,new String[]{"*"},null,null,null,null,null);
        while (cursor.moveToNext()){
            String c1 =cursor.getString(cursor.getColumnIndex(DBHelper.COL1_TADMIN));
            if (c1.equals(id_admin)){
                nome.setText("Nome: " + cursor.getString(cursor.getColumnIndex(DBHelper.COL2_TADMIN)));
                idade.setText("Idade: " + cursor.getString(cursor.getColumnIndex(DBHelper.COL3_TADMIN)));

                if(cursor.getString(cursor.getColumnIndex(DBHelper.COL5_TADMIN)).equals("1"))
                    sexo.setText("Sexo: Masculino");
                else
                    sexo.setText("Sexo: Feminino");

                morada.setText("morada: "+cursor.getString(cursor.getColumnIndex(DBHelper.COL4_TADMIN)));
                contacto.setText("contacto: "+cursor.getString(cursor.getColumnIndex(DBHelper.COL6_TADMIN)));
                break;
            }
        }

        editar_admin = findViewById(R.id .btnEditarEduc);
        editar_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editar_educ = new Intent(Activity_PerfAdmin.this, Activity_EditarAdmins.class);
                editar_educ.putExtra("id", id_admin);
                startActivity(editar_educ);
            }
        });

    }
}