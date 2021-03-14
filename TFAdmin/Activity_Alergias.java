package pt.ubi.di.pdm.tfadmin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class Activity_Alergias extends AppCompatActivity {

    LinearLayout oLL;
    Cursor oCursor;
    DBHelper dbHelper;
    SQLiteDatabase base;
    Button submeter,cancelar,add;
    EditText addAler;
    String tk,i;
    int e_id,id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alergias);

        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();

        SharedPreferences shp = getApplicationContext().getSharedPreferences("important_variables",0);
        id = shp.getInt("id",999);


        Intent Cheguei = getIntent();
        i = Cheguei.getStringExtra("id");
        e_id = Integer.parseInt(i);

        Log.d("tag","1");

        submeter = findViewById(R.id.btnSubmeterAlergias);
        cancelar = findViewById(R.id.btnCancelarAlergias);
        add = findViewById(R.id.btnAddAlergia);
        addAler = findViewById(R.id.addAlergia);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.addAlergia(id,e_id,addAler.getText().toString());
                addAler.setText("");
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        submeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    protected void onResume() {
        super.onResume();
        Cursor cursor = base.query(DBHelper.TEDUCADOR,new String[]{DBHelper.COL7_TEDUCADOR},DBHelper.COL1_TEDUCADOR+"=?",new String[]{String.valueOf(e_id)},null,null,null);
        cursor.moveToFirst();
        tk = cursor.getString(0);
        cursor.close();

        Log.d("TOKEN",tk);
        int aux = dbHelper.updateAlergia(base,id,e_id,tk);

        Log.d("tag","3");
        displayAlergias();
    }
    //Ajudei aqui também com algumas pequenas alterações para o código ficar bem - Tiago Almeida
    public void displayAlergias(){
        oLL = findViewById(R.id.listaAlergias);
        oLL.removeAllViews();
        oCursor = base.query(DBHelper.TALERGIA, new String[]{DBHelper.COL3_TALERGIA,DBHelper.COL2_TALERGIA}, null, null, DBHelper.COL3_TALERGIA, null, null, null);
        boolean bCarryOn = oCursor.moveToFirst();
        while (bCarryOn) {
            LinearLayout oLL1 = (LinearLayout) getLayoutInflater().inflate(R.layout.linha_alergia, null);
            oLL1.setId(oCursor.getInt(1) * 10 + 3);

            final EditText E1 = oLL1.findViewById(R.id.nomeAluno);
            E1.setId(oCursor.getInt(1) * 10 +2);
            E1.setText(oCursor.getString(0));

            ImageButton B1 = oLL1.findViewById(R.id.btnApagar);
            B1.setId(oCursor.getInt(1) * 10 +1);
            B1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbHelper.deleteAlergia(id,e_id,((v.getId()) -1) /10);
                    ((LinearLayout)(v.getParent()).getParent()).removeView(oLL1);
                }
            });

            ImageButton B2 = oLL1.findViewById(R.id.btnVerAluno);
            B2.setId(oCursor.getInt(1) * 10 );
            B2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     dbHelper.editAlergia(id, e_id, (((v.getId())) / 10) , E1.getText().toString());
                }
            });

            oLL.addView(oLL1);
            bCarryOn = oCursor.moveToNext();
        }

    }

}
