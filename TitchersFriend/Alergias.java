package pt.ubi.di.pdm.titchersfriend;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import java.util.ArrayList;

public class Alergias extends AppCompatActivity {
    DBHelper oDBH;
    SQLiteDatabase oSQLDB;
    Cursor oCursor;
    LinearLayout oLL;
    Button add, sub, canc;
    DBHelper dbHelper;
    EditText addA;
    ArrayList<String> subm = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alergias);
        dbHelper = new DBHelper(this);
        oDBH = new DBHelper(this);
        oSQLDB = oDBH.getWritableDatabase();
        add = (Button)findViewById(R.id.btnAddAlergia);
        sub = (Button)findViewById(R.id.btnSubmeterAlergias);
        canc = (Button)findViewById(R.id.btnCancelarAlergias);
        addA= (EditText)findViewById(R.id.addAlergia);
        displayAlunos();
        canc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED,null);
                finish();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addA.getText().toString().equals("")){
                    Toast.makeText(Alergias.this,"Preencha antes de tentar adicionar ou cancele",Toast.LENGTH_SHORT).show();
                    return;
                }
                ContentValues oCV = new ContentValues();
                oCV.put(dbHelper.COL2_T3,addA.getText().toString());
                oSQLDB.insert(dbHelper.TABLE_NAME3,null,oCV);
                Toast.makeText(Alergias.this,"Adicionado com sucesso",Toast.LENGTH_SHORT).show();
                oLL.removeAllViews();
                displayAlunos();
            }
        });
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(subm.size()<1){
                    Toast.makeText(Alergias.this,"Escolha alguma alergia ou cancele",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",subm.toString());
                setResult(Activity.RESULT_OK,returnIntent);
                Toast.makeText(Alergias.this,"Adicionado com sucesso",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    public void displayAlunos() {

        oLL = (LinearLayout) findViewById(R.id.listaAlergias);
        oCursor = oSQLDB.query(oDBH.TABLE_NAME3, new String[]{"*"}, null, null, null, null, null, null);

        boolean bCarryOn = oCursor.moveToFirst();
        while (bCarryOn) {
            LinearLayout oLL1 = (LinearLayout) getLayoutInflater().inflate(R.layout.linha_faltas, null);
            oLL1.setId(oCursor.getInt(0) * 10 + 2);

            TextView E1 = (TextView) oLL1.findViewById(R.id.nomeAluno);
            E1.setId(oCursor.getInt(0) * 10 + 1);
            E1.setText(oCursor.getString(1));

         final  CheckBox T2 = (CheckBox) oLL1.findViewById(R.id.checkBox);
            T2.setId(oCursor.getInt(0) * 10);
            T2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(T2.isChecked()){
                        Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
                        T2.setBackground(d1);
                      String id = String.valueOf((v.getId())/10);
                      subm.add(id);

                    }else{
                        Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkbox,null);
                        T2.setBackground(d1);
                        String id = String.valueOf((v.getId())/10);
                        subm.remove(id);

                    }
                }
            });
            oLL.addView(oLL1);
            bCarryOn = oCursor.moveToNext();
        }

    }

}
