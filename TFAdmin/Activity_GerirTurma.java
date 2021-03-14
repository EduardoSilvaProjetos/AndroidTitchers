package pt.ubi.di.pdm.tfadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_GerirTurma extends AppCompatActivity {

    Button GerirAlunos,GerirAulas,GerirFaltas,Alergias;
    String id;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerirturma);

        Intent Cheguei = getIntent();
        id = Cheguei.getStringExtra("id");
        Log.d("tag",id);

        GerirAlunos = (Button)findViewById(R.id.btnGerirAlunos);
        GerirAulas = (Button)findViewById(R.id.btnGerirAulas);
        GerirFaltas = (Button)findViewById(R.id.btnGerirFaltas);
        Alergias = (Button)findViewById(R.id.btnAlergias);

        GerirAlunos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Activity_GerirTurma.this,Activity_GerirAlunos.class);
                i.putExtra("id",id);
                startActivity(i);
            }
        });

        GerirAulas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Activity_GerirTurma.this,Activity_GerirAulas.class);
                i.putExtra("id",id);
                startActivity(i);
            }
        });

        GerirFaltas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Activity_GerirTurma.this,Activity_GerirFaltas.class);
                i.putExtra("id",id);
                startActivity(i);
            }
        });

        Alergias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Activity_GerirTurma.this,Activity_Alergias.class);
                i.putExtra("id",id);
                startActivity(i);
            }
        });


    }
}
