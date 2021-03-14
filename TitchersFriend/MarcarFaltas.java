package pt.ubi.di.pdm.titchersfriend;

import android.content.ContentValues;
import android.content.Intent;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class MarcarFaltas extends AppCompatActivity {
    DBHelper dbHelper;
    SQLiteDatabase base;
    LinearLayout oLL;
    Button submeter,cancelar;
    String id_at = "";
    ArrayList<String> faltas = new ArrayList<String>();
    ArrayList<String> pres = new ArrayList<String>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marcarfaltas);

        //Abre a base de dados local
        dbHelper = new DBHelper(this);
        base = dbHelper.getWritableDatabase();

        //iniciação de widgets
        submeter = (Button)findViewById(R.id.btnSubmeterFaltas);
        cancelar = (Button)findViewById(R.id.btnCancelarFaltas);

        //coloca a tada atual na variavel cD no formato dd/mm/aa
        Calendar calendar = Calendar.getInstance();
        String cD = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());


        displayAlunos();

        //id da atividade
        id_at = VerificaAtividade(cD);

        //Se id_at for "vazio" é porque ainda não foi criada atividade para o dia de hj
        if(id_at.equals("vazio")){
            Toast.makeText(MarcarFaltas.this,"Tem que criar uma atividade primeiro",Toast.LENGTH_SHORT).show();
        }

        //No botão submeter sao editadas as faltas de acordo com as chekbox
        //há duas arrayList (faltas e pres)
        //Na arrayList das faltas são colocados os ids do aluno, que tem checkbox marcada
        //Na arrayList das pres o contrário
        //Primeiro é feita a verificação se dos alunos do arrayList das faltas, algum ja tem falta marcada (caso tenha remove-se do arrayList)
        //Verifica-se se algum dos alunos no arrayList pres, tem falta marcada (caso tenha, retira-se a falta)
        //Marca-se falta aos aluno com id no arrayList faltas
        submeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id_at.equals("vazio")){
                    Toast.makeText(MarcarFaltas.this,"Tem que criar uma atividade primeiro",Toast.LENGTH_SHORT).show();
                    return;
                }

                Cursor cursor =base.query(dbHelper.TABLE_NAME4,new String[]{"*"},null,null,null,null,null);
                while (cursor.moveToNext()){
                    String c2 =cursor.getString(cursor.getColumnIndex(dbHelper.COL1_T4));
                    String c1 =cursor.getString(cursor.getColumnIndex(dbHelper.COL2_T4));
                    for(int i=0;i<faltas.size();i++){
                        if (c2.equals(faltas.get(i))&&c1.equals(id_at)){
                            faltas.remove(i);
                        }
                    }
                    for(int i=0;i<pres.size();i++){
                        if(c2.equals(pres.get(i))&&c1.equals(id_at)){
                            base.delete(dbHelper.TABLE_NAME4,
                                    dbHelper.COL1_T4 + "=?", new String[]{c2});
                        }
                    }
                }

                ContentValues oCV = new ContentValues();
                for (int i=0;i<faltas.size();i++){
                    oCV.put(dbHelper.COL1_T4,faltas.get(i));
                    oCV.put(dbHelper.COL2_T4,id_at);

                    base.insert(dbHelper.TABLE_NAME4,null,oCV);
                    Toast.makeText(MarcarFaltas.this,"Faltas marcadas!",Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    protected void onPause() {
        super.onPause();
        dbHelper.close();
    }

    protected void onResume() {
        super.onResume();
        base = dbHelper.getWritableDatabase();
        oLL.removeAllViews();
        displayAlunos();
    }


    //Metodo para fazer o display dos alunos
    //Os alunos que já têm falta marcada começam com a chckbox checked
    //Os alunos checked ficam com o id na arrayList faltas
    //os outros com o id na arrayList pres
    public void displayAlunos() {
        oLL = (LinearLayout) findViewById(R.id.verfaltas);
        Cursor oCursor = base.query(dbHelper.TABLE_NAME1, new String[]{"*"}, null, null, null, null, null, null);

        boolean bCarryOn = oCursor.moveToFirst();
        while (bCarryOn) {
            LinearLayout oLL1 = (LinearLayout) getLayoutInflater().inflate(R.layout.linha_faltas, null);
            oLL1.setId(oCursor.getInt(0) * 10 + 2);

            TextView E1 = (TextView) oLL1.findViewById(R.id.nomeAluno);
            E1.setId(oCursor.getInt(0) * 10 + 1);
            E1.setText(oCursor.getString(1));


            final CheckBox oB1 = (CheckBox) oLL1.findViewById(R.id.checkBox);
            oB1.setId(oCursor.getInt(0) * 10);
            if (VerificaFalta(id_at,oCursor.getString(0))){
                oB1.setChecked(true);
                Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
                oB1.setBackground(d1);
            }

            oB1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(oB1.isChecked()){
                        Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkmark,null);
                        oB1.setBackground(d1);
                        faltas.add(String.valueOf((v.getId())/10));
                        pres.remove(String.valueOf((v.getId())/10));

                    }else{
                        Drawable d1 = ResourcesCompat.getDrawable(getResources(),R.drawable.checkbox,null);
                        oB1.setBackground(d1);
                        faltas.remove(String.valueOf((v.getId())/10));
                        pres.add(String.valueOf((v.getId())/10));
                    }
                }
            });


            oLL.addView(oLL1);
            bCarryOn = oCursor.moveToNext();
        }

    }

    //Metodo para verificar se o aluno já tem falta marcada na atividade atual
    public boolean VerificaFalta(String id_at,String id){
        Cursor cursor =base.query(dbHelper.TABLE_NAME4,new String[]{"*"},null,null,null,null,null);
        while (cursor.moveToNext()){
            String c1 =cursor.getString(cursor.getColumnIndex(dbHelper.COL1_T4));
            String c2 =cursor.getString(cursor.getColumnIndex(dbHelper.COL2_T4));
            if(c1.equals(id) && c2.equals(id_at)){
                return true;
            }
        }
        return false;
    }


    //Metodo para verificar se a atividade atual ja foi criada
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
