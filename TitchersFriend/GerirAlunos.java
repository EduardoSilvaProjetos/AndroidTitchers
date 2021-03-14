package pt.ubi.di.pdm.titchersfriend;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GerirAlunos extends AppCompatActivity {

    DBHelper oDBH;
    SQLiteDatabase oSQLDB;
    Cursor oCursor;
    LinearLayout oLL;
    Button add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geriralunos);

        //iniciação de widgets e abertura da base de dados local
        add = (Button)findViewById(R.id.btnAddAluno);
        oDBH = new DBHelper(this);
        oSQLDB = oDBH.getWritableDatabase();

        displayAlunos();

        //direciona para a pagina AdicionarAluno
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent A2 = new Intent(GerirAlunos.this,AdicionarAluno.class);
                finish();
                startActivity(A2) ;
            }
        });
    }
        @Override
        protected void onPause() {
            super.onPause();
            oDBH.close();
        }
        @Override
        public void onResume() {
            super.onResume();
            oSQLDB= oDBH.getWritableDatabase();
            oLL.removeAllViews();
            displayAlunos();
        }

        //metodo para fazer o display dos alunos a partir da tabela "Educando"
    public void displayAlunos() {
        oLL = (LinearLayout) findViewById(R.id.visualizar);
        oCursor = oSQLDB.query(oDBH.TABLE_NAME1, new String[]{"*"}, null, null, null, null, null, null);

        boolean bCarryOn = oCursor.moveToFirst();
        while (bCarryOn) {
            LinearLayout oLL1 = (LinearLayout) getLayoutInflater().inflate(R.layout.linha_visualizar, null);
            oLL1.setId(oCursor.getInt(0) * 10 + 4);

            TextView E1 = (TextView) oLL1.findViewById(R.id.nomeAluno);
            E1.setId(oCursor.getInt(0) * 10 + 3);
            E1.setText(oCursor.getString(1));

            TextView T2 = (TextView) oLL1.findViewById(R.id.idAluno);
            T2.setId(oCursor.getInt(0) * 10 + 2);
            T2.setText(oCursor.getString(0));

            //direciona para a pagina do perfil do aluno
            ImageButton oB1 = (ImageButton) oLL1.findViewById(R.id.btnVerAluno);
            oB1.setId(oCursor.getInt(0) * 10 +1);
            oB1.setOnClickListener(new View.OnClickListener() {
                @Override
                    public void onClick(View v) {
                        int id = (v.getId())/10;
                        Intent profile = new Intent(GerirAlunos.this, PerfAlunoActivity.class);
                        profile.putExtra("id",String.valueOf(id));
                        startActivity(profile);
                    }
                });

            //Botão para eliminar um aluno (apaga o aluno da tabela educando e todos os seus registos nas outras tabelas)
            ImageButton oB2 = (ImageButton) oLL1.findViewById(R.id.btnApagar);
            oB2.setId(oCursor.getInt(0) * 10);
            oB2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    oSQLDB.delete(oDBH.TABLE_NAME1,
                            "ROWID="+(v.getId())/10,
                            null);

                    oSQLDB.delete(oDBH.TABLE_NAME6,
                            oDBH.COL2_T6+"=?",new String[]{(String.valueOf((v.getId())/10))});

                    oSQLDB.delete(oDBH.TABLE_NAME4,
                            oDBH.COL1_T4+"=?",new String[]{(String.valueOf((v.getId())/10))});

                    oSQLDB.delete(oDBH.TABLE_NAME5,
                            oDBH.COL6_T5+"=?",new String[]{(String.valueOf((v.getId())/10))});


                    LinearLayout oLL1 =(LinearLayout)findViewById((v.getId())+4);
                    ((LinearLayout)oLL1.getParent()).removeView(oLL1);

                }
            });


                oLL.addView(oLL1);
                bCarryOn = oCursor.moveToNext();
            }

    }
}
