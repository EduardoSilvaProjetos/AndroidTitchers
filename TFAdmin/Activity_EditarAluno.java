package pt.ubi.di.pdm.tfadmin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Activity_EditarAluno extends AppCompatActivity {
    DBHelper dbHelper;
    SQLiteDatabase base;
    EditText nome,idade,morada,contacto;
    Spinner sexo;
    Button submeter,cancelar;
    String id_aluno;
    int admin_id,id_al,id_educ;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editaraluno);

        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();

        Intent Cheguei = getIntent();
        id_aluno = Cheguei.getStringExtra("id");
        id_educ = Integer.parseInt(Cheguei.getStringExtra("id_educadora"));
        id_al = Integer.parseInt(id_aluno);

        SharedPreferences shp = getApplicationContext().getSharedPreferences("important_variables",0);
        admin_id = shp.getInt("id",999);

        nome = findViewById(R.id.editNome);
        idade = findViewById(R.id.editIdade);
        sexo = findViewById(R.id.editSexo);
        morada = findViewById(R.id.editMorada);
        contacto = findViewById(R.id.editContacto);

        submeter = findViewById(R.id.btnRelatorio);
        cancelar = findViewById(R.id.btnCancelarRel);

        String[] items = new String[]{"Sexo: Feminino", "Sexo: Masculino"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Activity_EditarAluno.this, R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sexo.setAdapter(adapter);

        Cursor cursor = base.query(DBHelper.TEDUCANDO,new String[]{"*"},DBHelper.COL1_TEDUCANDO+"=?",new String[]{id_aluno},null,null,null);
        cursor.moveToNext();

        nome.setText(cursor.getString(1));
        idade.setText(cursor.getString(2));

        if (cursor.getString(4).equals("1"))
            sexo.setSelection(1);
        else
            sexo.setSelection(0);

        morada.setText(cursor.getString(3));
        contacto.setText(cursor.getString(5));
        cursor.close();
        submeter.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String n = nome.getText().toString();
                int i = Integer.parseInt(idade.getText().toString());
                String m = morada.getText().toString();
                int s = sexo.getSelectedItemPosition();
                String c = contacto.getText().toString();


                Cursor cursor = base.query(DBHelper.TALERGIA,new String[]{"*"},DBHelper.COL1_TALERGIA+"=?",new String[]{id_aluno},null,null,null);

                ArrayList<Integer> aler = new ArrayList<Integer>();

                int[] aler2 = {};
                cursor.moveToFirst();


                while (cursor.moveToNext()){
                   aler.add(Integer.parseInt(cursor.getString(1)));
                }
                cursor.close();
                aler2 = aler.stream().mapToInt(Integer::intValue).toArray();

                int aux;
                aux = dbHelper.editEducando(admin_id,id_educ,id_al,n,i,m,s,c,aler2);

                if (aux == 1)
                    Toast.makeText(Activity_EditarAluno.this,"Sucesso",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(Activity_EditarAluno.this,"Erro",Toast.LENGTH_SHORT).show();

                dbHelper.close();
                finish();
            }
        });

    }
    @Override
    protected void onPause() {
        super.onPause();
        base.close();
    }
    @Override
    protected void onResume() {
        super.onResume();
        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();
    }


}
