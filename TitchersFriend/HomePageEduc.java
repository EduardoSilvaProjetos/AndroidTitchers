package pt.ubi.di.pdm.titchersfriend;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutionException;

public class HomePageEduc extends AppCompatActivity {

   ImageButton gerirAlunos,gerirAulas,MudarPassword,faltas;
    Button exit;
DBHelper dbHelper;
SQLiteDatabase base;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepageeduc);

        //Abrir a base de dados local
        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();
        //iniciação de widgets (Os image button são o menu)
        gerirAlunos = (ImageButton)findViewById(R.id.gerirAlunos) ;
        gerirAulas = (ImageButton)findViewById(R.id.gerirAulas) ;
        MudarPassword = (ImageButton)findViewById(R.id.mudarPass) ;
        faltas = (ImageButton)findViewById(R.id.gerirEduc) ;
        exit =(Button)findViewById(R.id.btnSair);

       gerirAlunos.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent A2 = new Intent(HomePageEduc.this,GerirAlunos.class);
               startActivity(A2) ;
           }
       });

        gerirAulas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent A2 = new Intent(HomePageEduc.this,GerirAulas.class);
                startActivity(A2) ;
            }
        });

        MudarPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent A2 = new Intent(HomePageEduc.this,MudarPassword.class);
                startActivity(A2) ;
            }
        });

        faltas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent A2 = new Intent(HomePageEduc.this,MarcarFaltas.class);
                startActivity(A2) ;
            }
        });
        //butão exit, limpa a base de dados local e diz ao servidor que ja não esta a usar as tabelas
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper.fechando(base,HomePageEduc.this);
                int id = 0;
                Cursor cursor =base.query(DBHelper.TABLE_NAME7,new String[]{"*"},null,null,null,null,null);
                if (cursor.moveToFirst()){
                    id = cursor.getInt(cursor.getColumnIndex(DBHelper.COL1_T7));
                }
                try {
                    String x = new Sender(HomePageEduc.this,"105","cs=1&id="+id,null).execute().get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                dbHelper.onDelete(base);
                finish();
            }

        });

    }
    @Override
    public void onPause() {
        super.onPause();
        base.close();
    }
    @Override
    public void onResume() {
        super.onResume();
        base = dbHelper.getWritableDatabase();
    }

    @Override
    public void onBackPressed() {//acrescentei isto para o utilizador não clicar no botão de sair sem querer
        Toast.makeText(HomePageEduc.this,"Clique no botão 'sair' para sair da aplicação",Toast.LENGTH_SHORT).show();
    }
}
