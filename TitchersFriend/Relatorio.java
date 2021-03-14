package pt.ubi.di.pdm.titchersfriend;

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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Relatorio extends AppCompatActivity {
    DBHelper dbHelper;
    SQLiteDatabase base;
    CheckBox comer,dormir,Wc,curativo,chorar;
    EditText notas;
    TextView nome;
    Button submeter,cancelar;
    String s = "";
    String id_at = "";
    String VerRel = "";
    String id = "";
    int v1=0,v2=0,v3=0,v4=0,modo = 0;


    //Apenas se pode fazer um relatorio por dia para cada aluno
    //A variavel mode é iniciada a 0 se o aluno ainda não tiver relatorio
    //se tiver relatorio passa a 1 (0-insert//1-update)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);
        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();

        //data atual
        Calendar calendar = Calendar.getInstance();
        String cD = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());

        //id do aluno
        Intent Cheguei = getIntent();
        id = Cheguei.getStringExtra("id");

        //id da atividade caso esta já tenha sido criada.
        id_at = VerificaAtividade(cD);
        //verifica se o aluno ja tem relatorio
        VerRel = VerificaRelatorio(id,cD);


        //iniciação de widgets
        nome = (TextView)findViewById(R.id.titulo_homeeduc);
        submeter = (Button) findViewById(R.id.btnSubmeterRel);
        cancelar = (Button) findViewById(R.id.btnCancelarRel);
        comer = (CheckBox)findViewById(R.id.cboxComer);
        dormir = (CheckBox)findViewById(R.id.cboxDormir);
        Wc = (CheckBox)findViewById(R.id.cboxWc);
        curativo = (CheckBox)findViewById(R.id.cboxMagoar);
        chorar = (CheckBox)findViewById(R.id.cboxChorar);
        notas = (EditText) findViewById(R.id.editTextTextPersonName);

        //se id_at for "vazio" é porque ainda não criou atividade
        if(id_at.equals("vazio")){
            Toast.makeText(Relatorio.this,"Tem que criar uma atividade primeiro",Toast.LENGTH_SHORT).show();
        }


        //Se verRel for "vazio" é porque ja foi feito relatorio para o aluno o modo passa a 1, e entra em modo edição
        //Os campos do relatorio são preenchidos com os dados do relatorio original
        if(VerRel.equals("vazio")){
            Toast.makeText(Relatorio.this,"Já fez um relatorio para este aluno, O relatorio antigo será substituido!",Toast.LENGTH_SHORT).show();
            Cursor cursor = base.query(dbHelper.TABLE_NAME5,new String[]{"*"},null,null,null,null,null);
            while (cursor.moveToNext()){
                String c2 =cursor.getString(cursor.getColumnIndex(dbHelper.COL6_T5));
                String c1 =cursor.getString(cursor.getColumnIndex(dbHelper.COL7_T5));
                if (c2.equals(id)){
                    if(c1.equals(VerificaAtividade(cD))){
                        notas.setText(cursor.getString(cursor.getColumnIndex(dbHelper.COL3_T5)));
                        if (cursor.getString(cursor.getColumnIndex(dbHelper.COL1_T5)).equals("1")){
                            comer.setChecked(true);
                            Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
                            comer.setBackground(d1);
                        }
                        if (cursor.getString(cursor.getColumnIndex(dbHelper.COL2_T5)).equals("1")){
                            dormir.setChecked(true);
                            Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
                            dormir.setBackground(d1);
                        }
                        if (cursor.getString(cursor.getColumnIndex(dbHelper.COL4_T5)).equals("1")){
                            Wc.setChecked(true);
                            Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
                            Wc.setBackground(d1);
                        }
                        if (cursor.getString(cursor.getColumnIndex(dbHelper.COL5_T5)).equals("1")){
                            curativo.setChecked(true);
                            Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
                            curativo.setBackground(d1);
                        }
                        if (cursor.getString(cursor.getColumnIndex(dbHelper.COL5_T5)).equals("2")){
                            chorar.setChecked(true);
                            Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
                            chorar.setBackground(d1);
                        }
                        if (cursor.getString(cursor.getColumnIndex(dbHelper.COL5_T5)).equals("3")){
                            curativo.setChecked(true);
                            chorar.setChecked(true);
                            Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
                            curativo.setBackground(d1);
                            chorar.setBackground(d1);
                        }
                    }

                }
            }
            modo = 1;
        }

        nome.setText(Cheguei.getStringExtra("nome"));

        comer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comer.isChecked()){
                    Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
                    /*comer.setCompoundDrawablesWithIntrinsicBounds(null,d1,null,null);
                    Toast.makeText(Relatorio.this,"Clicou aqui",Toast.LENGTH_SHORT).show();*/
                    comer.setBackground(d1);
                }else{
                    Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkbox,null);
                    comer.setBackground(d1);
                }
            }
        });

        dormir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dormir.isChecked()){
                    Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
                    /*comer.setCompoundDrawablesWithIntrinsicBounds(null,d1,null,null);
                    Toast.makeText(Relatorio.this,"Clicou aqui",Toast.LENGTH_SHORT).show();*/
                    dormir.setBackground(d1);
                }else{
                    Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkbox,null);
                    dormir.setBackground(d1);
                }
            }
        });

        Wc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Wc.isChecked()){
                    Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
                    /*comer.setCompoundDrawablesWithIntrinsicBounds(null,d1,null,null);
                    Toast.makeText(Relatorio.this,"Clicou aqui",Toast.LENGTH_SHORT).show();*/
                    Wc.setBackground(d1);
                }else{
                    Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkbox,null);
                    Wc.setBackground(d1);
                }
            }
        });

        curativo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(curativo.isChecked()){
                    Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
                    /*comer.setCompoundDrawablesWithIntrinsicBounds(null,d1,null,null);
                    Toast.makeText(Relatorio.this,"Clicou aqui",Toast.LENGTH_SHORT).show();*/
                    curativo.setBackground(d1);
                }else{
                    Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkbox,null);
                    curativo.setBackground(d1);
                }
            }
        });

        chorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chorar.isChecked()){
                    Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
                    /*comer.setCompoundDrawablesWithIntrinsicBounds(null,d1,null,null);
                    Toast.makeText(Relatorio.this,"Clicou aqui",Toast.LENGTH_SHORT).show();*/
                    chorar.setBackground(d1);
                }else{
                    Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkbox,null);
                    chorar.setBackground(d1);
                }
            }
        });

        //No botão submeter é feito o insert ou update
        //as variaveis v1 a v4 podem ser 0 ou 1 (não ou sim)
        //sao iniciadas a 0 e se as checbox forem checked, passam a 1
        //No fim é feito o intent para a pagina confereRelatorio, onde o utilizador pode escolher entre enviar para os pais em pdf ou não
        submeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(id_at.equals("vazio")){
                    Toast.makeText(Relatorio.this,"Tem que criar uma atividade primeiro",Toast.LENGTH_SHORT).show();
                    return;
                }


                if(comer.isChecked()){
                    v1 = 1;
                }
                if(dormir.isChecked()){
                    v2 = 1;
                }
                if(Wc.isChecked()){
                    v3 = 1;
                }
                if(curativo.isChecked()){
                    v4 = 1;
                }

                if(chorar.isChecked()){
                    v4 = 2;
                }

                if(chorar.isChecked()&&curativo.isChecked()){
                    v4 = 3;
                }

                s = notas.getText().toString();

                ContentValues oCV = new ContentValues();

                oCV.put(dbHelper.COL1_T5,v1);
                oCV.put(dbHelper.COL2_T5,v2);
                oCV.put(dbHelper.COL3_T5,s);
                oCV.put(dbHelper.COL4_T5,v3);
                oCV.put(dbHelper.COL5_T5,v4);
                oCV.put(dbHelper.COL6_T5,id);
                oCV.put(dbHelper.COL7_T5,id_at);

                if(modo == 0)
                    base.insert(dbHelper.TABLE_NAME5,null,oCV);
                if(modo == 1)
                    base.update(dbHelper.TABLE_NAME5,oCV,dbHelper.COL6_T5+"=? AND "+dbHelper.COL7_T5+"=?",new String[]{String.valueOf(id),String.valueOf(id_at)});

                Intent i = new Intent(Relatorio.this,ConferelatorioActivity.class);
                i.putExtra("id",id);
                i.putExtra("id_at",id_at);

                startActivity(i);

                //Toast.makeText(Relatorio.this,"Sucesso",Toast.LENGTH_SHORT).show();

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
        dbHelper.close();
    }
    @Override
    public void onResume() {
        super.onResume();
        base=dbHelper.getWritableDatabase();

    }

    public String VerificaRelatorio(String id,String date){

        Cursor cursor = base.query(dbHelper.TABLE_NAME5,new String[]{"*"},null,null,null,null,null);
        while (cursor.moveToNext()){
            String c2 =cursor.getString(cursor.getColumnIndex(dbHelper.COL6_T5));
            String c1 =cursor.getString(cursor.getColumnIndex(dbHelper.COL7_T5));
            if (c2.equals(id)){
                if(c1.contains(VerificaAtividade(date)))
                    return "vazio";
            }
        }
        return "ok";
    }


    public String VerificaAtividade(String date){

        Cursor cursor =base.query(dbHelper.TABLE_NAME2,new String[]{"*"},null,null,null,null,null);
        while (cursor.moveToNext()){
            String c2 =cursor.getString(cursor.getColumnIndex(dbHelper.COL1_T2));
            String c1 =cursor.getString(cursor.getColumnIndex(dbHelper.COL3_T2));
            if (c1.contains(date)){
                return c2;
            }
        }
        return "vazio";
    }

}


