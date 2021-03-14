package pt.ubi.di.pdm.titchersfriend;


import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.view.*;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class RegisterActivity extends AppCompatActivity {

    //Metodo para passar a password para Hash
    public static String getM5(String input) {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    Button btnCriar;
    EditText inputpass,inputEmail,inputRepPass,inputUser,inputIdade,inputMorada;
    Spinner dropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adduser);

        //iniciação de widgets
        btnCriar = (Button)findViewById(R.id.btnCriar) ;
        inputpass = (EditText) findViewById(R.id.inputPass);
        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputUser = (EditText) findViewById(R.id.inputUser);
        inputIdade = (EditText) findViewById(R.id.inputIdade);
        inputMorada = (EditText) findViewById(R.id.inputMorada);
        dropdown = (Spinner) findViewById(R.id.inputSexo);

        //conteudo para os spinners e sua atribuição
        String[] items = new String[]{"Masculino", "Feminino"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterActivity.this, R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        dropdown.setAdapter(adapter);



        btnCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x ="";
                String us = inputUser.getText().toString();
                String pass = inputpass.getText().toString();
                String email = inputEmail.getText().toString();
                String Idade = inputIdade.getText().toString();
                String Morada = inputMorada.getText().toString();
                int Sexo = 0;

                //Verificar se os campos estão todos preenchidos
                if(us.isEmpty() || pass.isEmpty() || email.isEmpty() || Idade.isEmpty() || Morada.isEmpty()){
                    Toast.makeText(RegisterActivity.this,"Preencha todos os campos!",Toast.LENGTH_SHORT).show();
                    return;
                }

                //checking if all parameters are correct
                if(us.contains(" ")){
                    inputUser.setText("");
                    Toast.makeText(RegisterActivity.this,"Username inválido! Não use espaços.",Toast.LENGTH_SHORT).show();
                    inputUser.requestFocus();
                    return;
                }

                //Verifica se o email cumpre os requesitos
                if(!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")){
                    inputEmail.setText("");
                    Toast.makeText(RegisterActivity.this,"Email inválido!",Toast.LENGTH_SHORT).show();
                    inputEmail.requestFocus();
                    return;
                }

                //Verifica se a password cumpre os requesitos
                if(!pass.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$")){
                    inputpass.setText("");

                    Toast.makeText(RegisterActivity.this,"Password inválida! 8 carateres (1 maiuscula, 1 minuscula e 1 dígito).",Toast.LENGTH_SHORT).show();
                    inputpass.requestFocus();
                    return;
                }

                //Verificar se as password conferem
                /*if(!passr.equals(pass)){
                    Toast.makeText(RegisterActivity.this,"Palavras-passe diferem",Toast.LENGTH_LONG).show();
                    inputpass.setText("");
                    inputRepPass.setText("");
                    return;
                }*/

                //Passa a password para Hash
                String enc = getM5(pass);

                //Sexo = 0 (Feminino)
                if(dropdown.getSelectedItem().toString().contains("Masculino")){
                    Sexo = 1;
                }

                //Executa a query 50 do php, com os dados necessarios para o registo e guarda a resposta na variavel x
                try {
                    x = new Sender(RegisterActivity.this,"50","nome="+us+"&idade="+Idade+"&morada="+Morada+"&sexo="+Sexo+"&email="+email+"&pass="+enc,null).execute().get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

                JSONObject reader = null;
                Boolean s = false;
                try {
                    reader = new JSONObject(x);
                    s = reader.getBoolean("success");
                    //guarda o conteudo de "sucess" na variave s (true/false)
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                
                //Se true...
                if(s){
                    Intent A2 = new Intent(RegisterActivity.this,Aprovacao.class);
                    startActivity(A2) ;
                    Toast.makeText(RegisterActivity.this,"Sucesso na criação da conta",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(RegisterActivity.this, "Erro na criação da conta, o seu nome de utilizador pode ser igual a um já utilizado, tente novamente",Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        });
    }
}
