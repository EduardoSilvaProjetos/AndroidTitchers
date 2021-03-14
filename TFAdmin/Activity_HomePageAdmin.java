package pt.ubi.di.pdm.tfadmin;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_HomePageAdmin extends AppCompatActivity {

    ImageButton gerir_alunos_button, gerir_aulas_button, mudar_pass_button, gerir_educ_button;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepageadmin);

        gerir_alunos_button = (ImageButton)findViewById(R.id.gerirUsers);
        gerir_aulas_button = (ImageButton)findViewById(R.id.gerirAulas);
        mudar_pass_button = (ImageButton)findViewById(R.id.mudarPass);
        gerir_educ_button = (ImageButton) findViewById(R.id.gerirEduc);

        gerir_alunos_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gerir_alunos = new Intent(Activity_HomePageAdmin.this, Activity_GerirUsers.class);
                startActivity(gerir_alunos);
            }
        });

        gerir_aulas_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gerir_aulas = new Intent(Activity_HomePageAdmin.this, Activity_GerirDados.class);
                startActivity(gerir_aulas);
            }
        });

        mudar_pass_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mudar_pass = new Intent(Activity_HomePageAdmin.this, Activity_MudarPass.class);
                startActivity(mudar_pass);
            }
        });

        gerir_educ_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Activity_HomePageAdmin.this.getSharedPreferences("important_variables", 0).edit().clear().apply();
               DBHelper dbHelper = new DBHelper(Activity_HomePageAdmin.this);
               SQLiteDatabase db = dbHelper.getWritableDatabase();
               dbHelper.delete(db);
               finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Activity_HomePageAdmin.this.getSharedPreferences("important_variables", 0).edit().clear().apply();
        DBHelper dbHelper = new DBHelper(Activity_HomePageAdmin.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.delete(db);
        finish();
    }
}
