package pt.ubi.di.pdm.tfadmin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_PerfAluno extends AppCompatActivity {

    DBHelper dbHelper;
    SQLiteDatabase base;
    TextView nome, idade, sexo, morada, contacto;
    Button rel,edit;
    String id_aluno,id_educadora;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfaluno);

        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();

        Intent Cheguei = getIntent();
        id_aluno = Cheguei.getStringExtra("id");
        id_educadora = Cheguei.getStringExtra("id_educadora");


        nome = (TextView) findViewById(R.id.txtNome);
        idade = (TextView) findViewById(R.id.txtIdade);
        sexo = (TextView) findViewById(R.id.txtSexo);
        morada = (TextView) findViewById(R.id.txtMorada);
        contacto = (TextView) findViewById(R.id.txtContacto);

        rel = (Button) findViewById(R.id.btnRelatorio);
        edit = (Button) findViewById(R.id.btnCancelarRel);


        Cursor cursor = base.query(DBHelper.TEDUCANDO,new String[]{"*"},DBHelper.COL1_TEDUCANDO+"=?",new String[]{id_aluno},null,null,null);
        cursor.moveToNext();

        nome.setText(cursor.getString(1));
        idade.setText(cursor.getString(2));

        if (cursor.getString(4).equals("1"))
            sexo.setText("Sexo: Masculino");
        else
            sexo.setText("Sexo: Feminino");

        morada.setText(cursor.getString(3));
        contacto.setText(cursor.getString(5));

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent e = new Intent(Activity_PerfAluno.this,Activity_EditarAluno.class);
                e.putExtra("id",id_aluno);
                e.putExtra("id_educadora",id_educadora);
                startActivity(e);
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        base.close();
    }
    @Override
    public void onResume() {
        super.onResume();
        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();

        SharedPreferences shp = getApplicationContext().getSharedPreferences("important_variables",0);
        int id = shp.getInt("id",999);


        String tk;
        Cursor cursor2 = base.query(DBHelper.TEDUCADOR,new String[]{DBHelper.COL7_TEDUCADOR},DBHelper.COL1_TEDUCADOR+"=?",new String[]{id_educadora},null,null,null);
        cursor2.moveToFirst();
        tk = cursor2.getString(0);
        cursor2.close();
        dbHelper.updateEducando(base,id,Integer.parseInt(id_educadora),tk);

        Cursor cursor = base.query(DBHelper.TEDUCANDO,new String[]{"*"},DBHelper.COL1_TEDUCANDO+"=?",new String[]{id_aluno},null,null,null);
        cursor.moveToNext();

        nome.setText(cursor.getString(1));
        idade.setText(cursor.getString(2));

        if (cursor.getString(4).equals("1"))
            sexo.setText("Sexo: Masculino");
        else
            sexo.setText("Sexo: Feminino");

        morada.setText(cursor.getString(3));
        contacto.setText(cursor.getString(5));

    }
}
