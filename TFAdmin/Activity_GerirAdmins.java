package pt.ubi.di.pdm.tfadmin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutionException;

public class Activity_GerirAdmins extends AppCompatActivity {

    DBHelper db_helper;
    SQLiteDatabase admin_db;
    LinearLayout visualizer;

    int id, aux;

    Button btn_espera, btn_add_admin;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geriradmins);

        db_helper = new DBHelper(this);
        admin_db = db_helper.getWritableDatabase();

        SharedPreferences oSP = getSharedPreferences("important_variables", 0);
        id = oSP.getInt("id", 999);

        btn_add_admin = (Button) findViewById(R.id.btnAddAdmin);
        btn_add_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adicionar_admin = new Intent(Activity_GerirAdmins.this,Activity_AdicionarAdmin.class);
                finish();
                startActivity(adicionar_admin);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        aux = db_helper.updateAdmin(admin_db, id);
        if(aux == 0 || aux == -1){
            Toast.makeText(Activity_GerirAdmins.this, "Erro", Toast.LENGTH_SHORT).show();
        }

        displayAdmin();
    }

    public void displayAdmin() {
        visualizer = (LinearLayout) findViewById(R.id.visualizar);
        visualizer.removeAllViewsInLayout();

        Cursor oCursor = admin_db.query(DBHelper.TADMIN, new String[]{"*"}, null, null, null, null, null, null);

        Boolean bCarryOn = oCursor.moveToFirst();
        while(bCarryOn){
            LinearLayout new_admin = (LinearLayout) getLayoutInflater().inflate(R.layout.linha_visualizar_educ, null);
            new_admin.setId(oCursor.getInt(0) * 10 + 4);

            TextView nome_admin = (TextView) new_admin.findViewById(R.id.nomeAluno);
            nome_admin.setId(oCursor.getInt(0) * 10 + 3);
            nome_admin.setText(oCursor.getString(1));

            TextView id_admin = (TextView) new_admin.findViewById(R.id.idAluno);
            id_admin.setId(oCursor.getInt(0) * 10 + 2);
            id_admin.setText(oCursor.getString(0));

            ImageButton btn_ver_admin = (ImageButton) new_admin.findViewById(R.id.btnVerAluno);
            btn_ver_admin.setId(oCursor.getInt(0) * 10 + 1);
            btn_ver_admin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Activity_GerirAdmins.this, Activity_PerfAdmin.class);
                    i.putExtra("id", String.valueOf((v.getId() - 1)/10));
                    startActivity(i);
                }
            });

            ImageButton btn_apagar_admin = (ImageButton) new_admin.findViewById(R.id.btnApagar);
            btn_apagar_admin.setId(oCursor.getInt(0) * 10);
            btn_apagar_admin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db_helper.deleteUser( id, (v.getId()/10));
                    Log.v("DEBUG", "id (5):" + id + ", user a apagar: " + (v.getId()/10));
                    LinearLayout user_to_delete = findViewById(v.getId() + 4);
                    ((LinearLayout) user_to_delete.getParent()).removeView(user_to_delete);
                }
            });

            visualizer.addView(new_admin);
            bCarryOn = oCursor.moveToNext();
        }
    }
}