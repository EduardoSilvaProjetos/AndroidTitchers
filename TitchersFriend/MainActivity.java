package pt.ubi.di.pdm.titchersfriend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.view.*;

import java.text.DateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    TextView oTV;
    Button btnCriaConta;
    Button btnLogin;
    MainActivity Refjanela;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Refjanela = this ;

        oTV = (TextView)findViewById(R.id.titulo);
        btnCriaConta = (Button)findViewById(R.id.btnCriarConta);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent A2 = new Intent(Refjanela,LoginActivity.class);
                startActivity(A2) ;
            }
        });

        btnCriaConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent A2 = new Intent(Refjanela,RegisterActivity.class);
                startActivity(A2) ;
            }
        });


        }

    }





