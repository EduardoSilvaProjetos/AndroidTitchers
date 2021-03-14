package pt.ubi.di.pdm.tfadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_GerirUsers extends AppCompatActivity {
    Button gerir_educ_button;
    Button gerir_admin_button;

    protected void onCreate(Bundle savedInstanceState) {
        //Log.v("DEBUG", "Starting the activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerirusers);

        //Log.v("DEBUG", "It failed here");
        gerir_educ_button = (Button) findViewById(R.id.btnGerirEducs);
        gerir_admin_button = (Button) findViewById(R.id.btnGerirAdmins);

        gerir_educ_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gerir_educs = new Intent(Activity_GerirUsers.this, Activity_GerirEducadores.class);
                startActivity(gerir_educs);
            }
        });

        gerir_admin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gerir_admins = new Intent(Activity_GerirUsers.this, Activity_GerirAdmins.class);
                startActivity(gerir_admins);
            }
        });
    }
}
