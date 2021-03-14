package pt.ubi.di.pdm.titchersfriend;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PerfAlunoActivity extends Activity {

    DBHelper dbHelper;
    SQLiteDatabase base;

    TextView nome,idade,sexo,morada,contacto;
    Button rel,edit;
    String id,Snome,Sidade,Ssexo,Smorada,Scontacto;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfaluno);

        //Abre a base de dados local
        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();

        //recebe o id do aluno por intent
        Intent Cheguei = getIntent();
        id = Cheguei.getStringExtra("id");


        //Este loop foi feito, antes de saber que podia usar o parametro "selection" para fazer a filtragem necessária.
        //De qualquer das formas, guarda os dados do aluno, para preencher os TextViews
        Cursor cursor =base.query(dbHelper.TABLE_NAME1,new String[]{"*"},null,null,null,null,null);
        while (cursor.moveToNext()){
            String c1 =cursor.getString(cursor.getColumnIndex(dbHelper.COL1_T1));
            if (c1.equals(id)){
                Snome = cursor.getString(cursor.getColumnIndex(dbHelper.COL2_T1));
                Sidade = cursor.getString(cursor.getColumnIndex(dbHelper.COL3_T1));
                Ssexo = cursor.getString(cursor.getColumnIndex(dbHelper.COL5_T1));
                Smorada = cursor.getString(cursor.getColumnIndex(dbHelper.COL4_T1));
                Scontacto = cursor.getString(cursor.getColumnIndex(dbHelper.COL6_T1));
                break;
            }
        }

        //iniciação dos widgets
        nome = (TextView)findViewById(R.id.txtNome);
        idade = (TextView)findViewById(R.id.txtIdade);
        sexo = (TextView)findViewById(R.id.txtSexo);
        morada = (TextView)findViewById(R.id.txtMorada);
        contacto = (TextView)findViewById(R.id.txtContacto);

        rel = (Button) findViewById(R.id.btnRelatorio);
        edit = (Button) findViewById(R.id.btnCancelarRel);

        nome.setText("Nome: "+Snome);
        idade.setText("Idade: "+Sidade);

        if(Ssexo.equals("1"))
            sexo.setText("Sexo: Masculino");
        else
            sexo.setText("Sexo: Feminino");


        morada.setText("Morada: "+Smorada);
        contacto.setText("Contacto: "+Scontacto);

        //direciona para a pagina para fazer o relatorio do aluno
        rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a1 = new Intent(PerfAlunoActivity.this,Relatorio.class);
                a1.putExtra("id",id);
                a1.putExtra("nome",nome.getText().toString());
                //Envia o id e nome do aluno por intent
                startActivity(a1);
            }
        });

        //direciona para a pagina para editar o aluno
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a2 = new Intent(PerfAlunoActivity.this, EditAlunoActivity.class);
                //envia o id do aluno por intent
                a2.putExtra("id",id);
                startActivity(a2);
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
        base = dbHelper.getWritableDatabase();
        Cursor cursor =base.query(dbHelper.TABLE_NAME1,new String[]{"*"},null,null,null,null,null);
        while (cursor.moveToNext()) {
            String c1 = cursor.getString(cursor.getColumnIndex(dbHelper.COL1_T1));
            if (c1.equals(id)) {
                nome.setText("Nome: "+cursor.getString(cursor.getColumnIndex(dbHelper.COL2_T1)));
                idade.setText("Idade: "+cursor.getString(cursor.getColumnIndex(dbHelper.COL3_T1)));

                Ssexo = cursor.getString(cursor.getColumnIndex(dbHelper.COL5_T1));
                if(Ssexo.equals("1"))
                    sexo.setText("Sexo: Masculino");
                else
                    sexo.setText("Sexo: Feminino");


                morada.setText("Morada: "+cursor.getString(cursor.getColumnIndex(dbHelper.COL4_T1)));
                contacto.setText("Contacto: "+cursor.getString(cursor.getColumnIndex(dbHelper.COL6_T1)));
                break;
            }
        }
    }



}
