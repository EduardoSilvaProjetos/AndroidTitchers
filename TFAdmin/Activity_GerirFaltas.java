package pt.ubi.di.pdm.tfadmin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class Activity_GerirFaltas extends AppCompatActivity {

    DBHelper dbHelper;
    SQLiteDatabase base;
    Cursor oCursor;
    LinearLayout oLL;
    Spinner dia,mes,ano;
    String data = "",i,tk;
    int id,e_id,at_id;
    Button pesquisa,submeter,cancelar;
    ArrayList<Integer> falt = new ArrayList<Integer>();
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerirfaltas);

        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();

        Log.d("tag","1");
        SharedPreferences shp = getApplicationContext().getSharedPreferences("important_variables",0);
        id = shp.getInt("id",999);

        Log.d("tag","1");
        Intent Cheguei = getIntent();
        i = Cheguei.getStringExtra("id");
        e_id = Integer.parseInt(i);

        Cursor cursor = base.query(DBHelper.TEDUCADOR,new String[]{DBHelper.COL7_TEDUCADOR},DBHelper.COL1_TEDUCADOR+"=?",new String[]{String.valueOf(e_id)},null,null,null);
        cursor.moveToFirst();
        tk = cursor.getString(0);
        cursor.close();

        dia = findViewById(R.id.inputDia);
        mes = findViewById(R.id.inputMes);
        ano = findViewById(R.id.inputAno);
        pesquisa = findViewById(R.id.button);
        submeter = findViewById(R.id.btnSubmeterRel);
        cancelar = findViewById(R.id.btnCancelarRel);

        Calendar cal = Calendar.getInstance();//ajudei aqui

        int year = cal.get(Calendar.YEAR);//ajudei aqui
        int month = cal.get(Calendar.MONTH);//ajudei aqui
        int day = cal.get(Calendar.DAY_OF_MONTH);//ajudei aqui

        String[] itemsDia = new String[]{"01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(Activity_GerirFaltas.this, R.layout.spinner_item, itemsDia);
        adapter1.setDropDownViewResource(R.layout.spinner_dropdown_item);
        dia.setAdapter(adapter1);
        dia.setSelection(day-1,true);

        String[] itemsMes = new String[]{"01","02","03","04","05","06","07","08","09","10","11","12"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(Activity_GerirFaltas.this, R.layout.spinner_item, itemsMes);
        adapter2.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mes.setAdapter(adapter2);
        mes.setSelection(month,true);//ajudei aqui

        String[] itemsAno = new String[]{"19","20","21","22"};
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(Activity_GerirFaltas.this, R.layout.spinner_item, itemsAno);
        adapter3.setDropDownViewResource(R.layout.spinner_dropdown_item);
        ano.setAdapter(adapter3);
        ano.setSelection(Arrays.asList(itemsAno).indexOf(String.valueOf(year%100)),true);


        pesquisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data = dia.getSelectedItem().toString() + "/" + mes.getSelectedItem().toString() + "/" + ano.getSelectedItem().toString();

                //Ajudei em toda esta parte daqui para baixo, basicamente isto vai à tabela das atividades e retorna um inteiro com o valor da atividade
                // depois se esse valor for 0 ou seja nenhuma atividade encontrada então dizemos que não foi encontrada a atividade e para aguardar a criação da aula- Tiago Almeida
                Cursor cursor2 = base.query(DBHelper.TATIVIDADE,new String[]{DBHelper.COL1_TATIVIDADE},DBHelper.COL3_TATIVIDADE+"= ?",new String[]{data},null,null,null);
                if(cursor2.getCount()<1){
                    Toast.makeText(Activity_GerirFaltas.this,"A aula neste dia não foi criada,por favor aguarde a sua criação para poder editá-la",Toast.LENGTH_LONG).show();
                    oLL = findViewById(R.id.visualizar);
                    oLL.removeAllViews();
                    return;
                }
                cursor2.moveToFirst();
                at_id = cursor2.getInt(0);
                if(at_id == 0){
                    Toast.makeText(Activity_GerirFaltas.this,"A aula neste dia não foi criada, por favor aguarde a sua criação para poder editá-la",Toast.LENGTH_LONG).show();
                    oLL = findViewById(R.id.visualizar);
                    oLL.removeAllViews();
                    return;
                }
                cursor2.close();
                dbHelper.updateFalta(base,id,e_id,tk,data);
                displayFaltas();
            }
        });

        submeter.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                int[] falt2 = {};
                falt2 = falt.stream().mapToInt(Integer::intValue).toArray();

                for (int i=0;i<falt2.length;i++)
                    Log.d("tag",String.valueOf(falt2[i]));

                int aux = dbHelper.editFalta(id,e_id,at_id,falt2);
               if(aux == 1){
                   Toast.makeText(Activity_GerirFaltas.this,"sucesso",Toast.LENGTH_SHORT).show();
               }else{

                   Toast.makeText(Activity_GerirFaltas.this,falt.toString(),Toast.LENGTH_SHORT).show();
               }
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        base.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        base = dbHelper.getWritableDatabase();
        dbHelper.updateAtividade(base,id,e_id,tk); //ajudei aqui Tiago Almeida
    }

    public void displayFaltas() {
        oLL = findViewById(R.id.visualizar);
        oLL.removeAllViews();
        oCursor = base.query(DBHelper.TFALTA, new String[]{"*"}, null, null, null, null, null, null);
        boolean bCarryOn = oCursor.moveToFirst();
        while (bCarryOn) {
            LinearLayout oLL1 = (LinearLayout) getLayoutInflater().inflate(R.layout.linha_faltas, null);
            oLL1.setId(oCursor.getInt(0) * 10 + 2);

            TextView E1 = oLL1.findViewById(R.id.nomeAluno);
            E1.setId(oCursor.getInt(0) * 10 );
            E1.setText(oCursor.getString(1));

            final CheckBox B1 = oLL1.findViewById(R.id.checkBox);
            B1.setId(oCursor.getInt(0)*10+1);

            if(oCursor.getString(2).equals("1")) {
                B1.setChecked(true);
                Drawable d1 = ResourcesCompat.getDrawable(getResources(), R.drawable.checkmark, null);
                B1.setBackground(d1);
                falt.add(Integer.parseInt(oCursor.getString(0)));
            }

            B1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(B1.isChecked()){
                        Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
                        B1.setBackground(d1);
                        falt.add((v.getId())/10);
                    }else{
                        Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkbox,null);
                        B1.setBackground(d1);

                        falt.remove(Integer.valueOf((v.getId())/10));
                    }
                }
            });
            oLL.addView(oLL1);
            bCarryOn = oCursor.moveToNext();
        }
        oCursor.close();
    }




}
