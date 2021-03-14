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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class Activity_GerirEducadores extends AppCompatActivity {

    DBHelper db_helper;
    SQLiteDatabase educ_db;
    LinearLayout visualizer;

    int id, aux;

    Button btn_espera, btn_Add_educ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerireduc);

        db_helper = new DBHelper(this);
        educ_db = db_helper.getWritableDatabase();

        SharedPreferences oSP = getApplicationContext().getSharedPreferences("important_variables", 0);
        id = oSP.getInt("id", 999);

        btn_espera = findViewById(R.id.btnEspera);
        btn_espera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_to_waiting_room = new Intent(Activity_GerirEducadores.this, Activity_WaitingRoomEduc.class);
                startActivity(go_to_waiting_room);
            }
        });

        btn_Add_educ = findViewById(R.id.btnAddEduc);
        btn_Add_educ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add_educ = new Intent(Activity_GerirEducadores.this, Activity_AdicionarEducadores.class);
                startActivity(add_educ);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        aux = db_helper.updateEducador(educ_db, id);
        if(aux == 0 || aux == -1){
            Toast.makeText(Activity_GerirEducadores.this, "Erro", Toast.LENGTH_SHORT).show();
        }

        displayEducadores();
    }

    public void displayEducadores() {
        visualizer = findViewById(R.id.visualizar);
        visualizer.removeAllViewsInLayout();

        Cursor oCursor = educ_db.query(DBHelper.TEDUCADOR, new String[]{"*"}, null, null, null, null, null, null);

        Boolean bCarryOn = oCursor.moveToFirst();
        while(bCarryOn){
            LinearLayout new_educ = (LinearLayout) getLayoutInflater().inflate(R.layout.linha_visualizar_educ, null);
            new_educ.setId(oCursor.getInt(0) * 10 + 4);

            TextView nome_educ = new_educ.findViewById(R.id.nomeAluno);
            nome_educ.setId(oCursor.getInt(0) * 10 + 3);
            nome_educ.setText(oCursor.getString(1));

            TextView id_educ = new_educ.findViewById(R.id.idAluno);
            id_educ.setId(oCursor.getInt(0) * 10 + 2);
            id_educ.setText(oCursor.getString(0));

            ImageButton btn_educ = new_educ.findViewById(R.id.btnVerAluno);
            btn_educ.setId(oCursor.getInt(0) * 10 + 1);
            btn_educ.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Activity_GerirEducadores.this, Activity_PerfEduc.class);
                    i.putExtra("id", String.valueOf((v.getId() - 1)/10));
                    startActivity(i);
                }
            });

            ImageButton btn_apagar_educ = new_educ.findViewById(R.id.btnApagar);
            btn_apagar_educ.setId(oCursor.getInt(0) * 10);
            btn_apagar_educ.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db_helper.deleteUser( id, (v.getId()/10));
                    Log.v("DEBUG", "id (5):" + id + ", user a apagar: " + (v.getId()/10));
                    LinearLayout user_to_delete = findViewById(v.getId() + 4);
                    ((LinearLayout) user_to_delete.getParent()).removeView(user_to_delete);
                }
            });

            visualizer.addView(new_educ);
            bCarryOn = oCursor.moveToNext();
        }
    }
}