package pt.ubi.di.pdm.titchersfriend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Aprovacao extends Activity {

    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aprovacao);

        btn = (Button)findViewById(R.id.btnVoltar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Aprovacao.this,MainActivity.class);
                startActivity(i);
            }
        });
    }

}
