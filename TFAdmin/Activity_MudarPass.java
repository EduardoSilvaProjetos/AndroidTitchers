package pt.ubi.di.pdm.tfadmin;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Activity_MudarPass extends AppCompatActivity {

    //esta função foi retirada da internet, faz a conversão de uma string para uma string com hash md5
    private static String getM5(String input) {
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
        }   // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mudarpass);

        Button btn_submeter = findViewById(R.id.btnSubmeter);
        Button btn_cancelar = findViewById(R.id.btnCancelar);

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_submeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText new_p = findViewById(R.id.inputPass);
                EditText new_p_rep = findViewById(R.id.inputRepPass);
                EditText old_p = findViewById(R.id.inputPassOld);

                String old_pwd = old_p.getText().toString();
                String new_pwd = new_p.getText().toString();
                String new_pwd_rep = new_p_rep.getText().toString();

                DBHelper dbHelper = new DBHelper(Activity_MudarPass.this);
                SharedPreferences oSP = getSharedPreferences("important_variables", 0);
                int id = oSP.getInt("id",0);
                if (id == 0){
                    finish();
                }


                if(!new_pwd.equals(new_pwd_rep)){
                    new_p.setText("");
                    new_p_rep.setText("");
                    Toast.makeText(Activity_MudarPass.this,"As passwords não são iguais",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(new_pwd.equals(old_pwd)){
                    old_p.setText("");
                    new_p.setText("");
                    new_p_rep.setText("");
                    Toast.makeText(Activity_MudarPass.this,"As passwords são todas iguais "+new_pwd+" _ "+old_pwd,Toast.LENGTH_SHORT).show();
                    return;
                }

                String hashp,hashp2;
                hashp = Activity_MudarPass.getM5(new_pwd);
                hashp2 = Activity_MudarPass.getM5(old_pwd);

                int i = dbHelper.changePassword(id,hashp,hashp2);

                switch (i){
                    case -1:
                        Toast.makeText(Activity_MudarPass.this,"Erro na comunicação dos dados verifique a sua ligação à internet",Toast.LENGTH_SHORT).show();
                    break;
                    case 0:
                        Toast.makeText(Activity_MudarPass.this,"Erro na comunicação dos dados",Toast.LENGTH_SHORT).show();
                    break;
                    case 1:
                        Toast.makeText(Activity_MudarPass.this,"Alterado com sucesso",Toast.LENGTH_SHORT).show();
                    break;
                }

                finish();

            }
        });


    }
}
