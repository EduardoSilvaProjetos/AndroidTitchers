package pt.ubi.di.pdm.tfadmin;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class Activity_AddAluno extends AppCompatActivity {
    Spinner dropdown;
    EditText educando, idade, morada, email;
    Button registo,alergia;
    DBHelper dbHelper;
    SQLiteDatabase oSQLDB;
    String result;
    ArrayList<String> myList = new ArrayList<>();
    ArrayList<Integer> aler = new ArrayList<>();


    int a1,admin_id,ed_id;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addaluno);
        myList.add("");
        dbHelper = new DBHelper(Activity_AddAluno.this);
        oSQLDB= dbHelper.getWritableDatabase();
        alergia = findViewById(R.id.btnAddAlergia);
        registo = findViewById(R.id.btnCriar);
        educando = findViewById(R.id.inputUser);
        idade = findViewById(R.id.inputIdade);
        morada = findViewById(R.id.inputMorada);
        email = findViewById((R.id.inputEmail));
        dropdown = findViewById(R.id.inputSexo);
        myList.clear();

        SharedPreferences shp = getApplicationContext().getSharedPreferences("important_variables",0);
        admin_id = shp.getInt("id",999);

        ed_id = Integer.parseInt(getIntent().getStringExtra("ed_id"));



        String[] items = new String[]{"Feminino", "Masculino"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Activity_AddAluno.this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                a1 = dropdown.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        registo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                String ed = educando.getText().toString();
                String id = idade.getText().toString();
                String mor = morada.getText().toString();
                String em = email.getText().toString();
                if(!em.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")){
                    email.setText("");
                    Toast.makeText(Activity_AddAluno.this,"Email inválido!",Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                }

                int[] aler2 = {};


                if (!myList.isEmpty()) {
                    for (int i = 0; i < myList.size(); i++) {
                        aler.add(Integer.parseInt(myList.get(i)));
                    }
                    aler2 = aler.stream().mapToInt(Integer::intValue).toArray();
                }


                dbHelper.addEducando(admin_id,ed_id,ed,Integer.parseInt(id),mor,a1,em,aler2);
                finish();
                }
            });


        alergia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Activity_AddAluno.this,Activity_AddAlergiaAluno.class);
                i.putExtra("ed_id",String.valueOf(ed_id));
                startActivityForResult(i, 2);
            }
        });



    }
    @Override
    public void onResume() {

        super.onResume();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {

            if(resultCode == Activity.RESULT_OK){
                result=data.getStringExtra("result");
                myList = new ArrayList(Arrays.asList(result.substring(1, result.length() - 1).replaceAll("\\s", "").split(",")));
                Log.d("MYLIST",myList.toString());
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                myList.clear();
            }
        }
    }//onActivityResult
}

