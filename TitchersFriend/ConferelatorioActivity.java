package pt.ubi.di.pdm.titchersfriend;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class ConferelatorioActivity extends Activity {
    Button btn,btn2;
    int com,dor,wc,cur,id,id_at;
    String not = "";
    DBHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confrelatorio);
        Intent i = getIntent();
        id = Integer.parseInt(i.getStringExtra("id"));
        id_at = Integer.parseInt(i.getStringExtra("id_at"));

        dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();
        String nome = null;
        Cursor c = db.query(DBHelper.TABLE_NAME1,new String[]{DBHelper.COL2_T1},DBHelper.COL1_T1+"=?",new String[]{String.valueOf(id)},null,null,null);
        if (c.moveToFirst()){
            nome = c.getString(0);
        }
        c.close();
        c = db.query(DBHelper.TABLE_NAME5,new String[]{DBHelper.COL1_T5,DBHelper.COL2_T5,DBHelper.COL3_T5,DBHelper.COL4_T5,DBHelper.COL5_T5},DBHelper.COL6_T5+"=? AND "+DBHelper.COL7_T5+"=?",new String[]{String.valueOf(id),String.valueOf(id_at)},null,null,null);
        if (c.moveToFirst()){
            com = c.getInt(0);
            dor = c.getInt(1);
            not = c.getString(2);
            wc = c.getInt(3);
            cur = c.getInt(4);
        }
        c.close();
        db.close();

        ((TextView) findViewById(R.id.textView3)).setText(nome);

        if(com == 1)
            ((TextView)(findViewById(R.id.outputComeu))).setText(getString(R.string.sim));
        else
            ((TextView)(findViewById(R.id.outputComeu))).setText(getString(R.string.nao));
        if(dor == 1)
            ((TextView)(findViewById(R.id.outputDormiu))).setText(getString(R.string.sim));
        else
            ((TextView)(findViewById(R.id.outputDormiu))).setText(getString(R.string.nao));
        if(wc == 1)
            ((TextView)(findViewById(R.id.outputWC))).setText(getString(R.string.sim));
        else
            ((TextView)(findViewById(R.id.outputWC))).setText(getString(R.string.nao));
        if(cur/10 == 1)
            ((TextView)(findViewById(R.id.outputChorou))).setText(getString(R.string.sim));
        else
            ((TextView)(findViewById(R.id.outputChorou))).setText(getString(R.string.nao));

        if (cur%10 == 1)
            ((TextView)(findViewById(R.id.outputMagoar))).setText(getString(R.string.sim));
        else
            ((TextView)(findViewById(R.id.outputMagoar))).setText(getString(R.string.nao));

        ((TextView)(findViewById(R.id.outputComentario))).setText(not);

        btn = (Button)findViewById(R.id.btnSubmeterRel);
        final ConferelatorioActivity ref = this;

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Tentativa de meter uma progress bar a funcionar falhou
                //Log.d("TAG","antes");
                createAndSendPDF();
                //Log.d("TAG","depois");
                finish();
            }
        });

        btn2 = (Button) findViewById(R.id.btnCancelarRel);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private static final int TEXT_TITLE = 40;
    private static final int TEXT_MINIATURE = 8;
    private static final int TEXT_PARAGRAPH = 12;

    /**
     * Função retirada de https://stackoverflow.com/questions/2336938/wrapping-long-text-on-an-android-canvas
     * This function draws the text on the canvas based on the x-, y-position.
     * If it has to break it into lines it will do it based on the max width
     * provided.
     *
     * @author Alessandro Giusa
     * @version 0.1, 14.08.2015
     * @param canvas
     *            canvas to draw on
     * @param paint
     *            paint object
     * @param x
     *            x position to draw on canvas
     * @param y
     *            start y-position to draw the text.
     * @param maxWidth
     *            maximal width for break line calculation
     * @param text
     *            text to draw
     */
    public static float drawTextAndBreakLine(final Canvas canvas, final Paint paint,
                                            final float x, final float y, final float maxWidth, final String text) {
        String textToDisplay = text;
        String tempText = "";
        char[] chars;
        float textHeight = paint.descent() - paint.ascent();
        float lastY = y;
        int nextPos = 0;
        int lengthBeforeBreak = textToDisplay.length();
        do {

            lengthBeforeBreak = textToDisplay.length();
            chars = textToDisplay.toCharArray();
            nextPos = paint.breakText(chars, 0, chars.length, maxWidth, null);
            tempText = textToDisplay.substring(0, nextPos);
            textToDisplay = textToDisplay.substring(nextPos, textToDisplay.length());
            canvas.drawText(tempText, x, lastY, paint);
            lastY += textHeight;
        } while(nextPos < lengthBeforeBreak);
        return lastY;
    }

    private String finalVeredict(int v){
        if(v%10 == 0){
            return getString(R.string.nao);
        }else{
            return getString(R.string.sim);
        }
    }
    private String finalVeredict2(int v){
        if(v/10 == 0){
            return getString(R.string.nao);
        }else{
            return getString(R.string.sim);
        }
    }
    private void createAndSendPDF(){
        int sheetHeight = 842;
        int sheetWidth = 595;
        float lastY = 90;

        PdfDocument myDocument = new PdfDocument();
        Paint myPaint = new Paint();

        PdfDocument.PageInfo myDocumentInfo = new PdfDocument.PageInfo.Builder(sheetWidth,sheetHeight,1).create();
        //dimensões em postscript de uma página A4 - 595x842
        PdfDocument.Page myPage1 = myDocument.startPage(myDocumentInfo);
        Canvas cnvs = myPage1.getCanvas();
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.fundo);
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap,sheetWidth,sheetHeight,false);
        //fundo da folha compatível com o fundo da app

        Typeface plain = ResourcesCompat.getFont(getApplicationContext(), R.font.kindergarten);
        myPaint.setTypeface(plain);
        cnvs.drawBitmap(scaled,0,0,myPaint);
        myPaint.setTextSize(TEXT_TITLE);
        lastY = drawTextAndBreakLine(cnvs,myPaint,40,lastY,sheetWidth-80,getString(R.string.app_name));
        myPaint.setTextSize(TEXT_PARAGRAPH);
        Typeface text = Typeface.create("Arial",Typeface.ITALIC);
        myPaint.setTypeface(text);

        String nome = null;
        String contacto = null;
        String sumario = " ";
        String data = null;
        int sexo = 0;

        Cursor c = db.query(DBHelper.TABLE_NAME1,new String[]{DBHelper.COL2_T1,DBHelper.COL6_T1,DBHelper.COL5_T1},DBHelper.COL1_T1+"=?",new String[]{String.valueOf(id)},null,null,null);
        if (c.moveToFirst()){
            nome = c.getString(0);
            contacto = c.getString(1);
            sexo = c.getInt(2);
        }
        c.close();
        c = db.query(DBHelper.TABLE_NAME2,new String[]{DBHelper.COL2_T2,DBHelper.COL3_T2},DBHelper.COL1_T2+"=?",new String[]{String.valueOf(id_at)},null,null,null);
        if (c.moveToFirst()){
            sumario = c.getString(0);
            data = c.getString(1);
        }
        c.close();

        lastY = 160;
        if(sexo == 1){
            lastY = drawTextAndBreakLine(cnvs,myPaint,40,lastY, sheetWidth-80,"Car@ encarregad@ de educação do aluno "+nome+",");
        }else{
            lastY = drawTextAndBreakLine(cnvs,myPaint,40,lastY, sheetWidth-80,"Car@ encarregad@ de educação da aluna "+nome+",");
        }
        lastY += 30;
        lastY = drawTextAndBreakLine(cnvs,myPaint,40,lastY, sheetWidth-80,"Este documento contém o relatório diário do dia "+data+".");
        lastY = drawTextAndBreakLine(cnvs,myPaint,40,lastY, sheetWidth-80," ");
        String [] ex = sumario.split("//");
        lastY = drawTextAndBreakLine(cnvs,myPaint,40,lastY, sheetWidth-80, ex[0]);
        lastY = drawTextAndBreakLine(cnvs,myPaint,40,lastY, sheetWidth-80, ex[1]);
        lastY = drawTextAndBreakLine(cnvs,myPaint,40,lastY, sheetWidth-80," ");
        if(sexo == 1){
            lastY = drawTextAndBreakLine(cnvs,myPaint,40,lastY, sheetWidth-80,"Neste dia o menino:");
        }else{
            lastY = drawTextAndBreakLine(cnvs,myPaint,40,lastY, sheetWidth-80,"Neste dia a menina:");
        }
        lastY +=TEXT_PARAGRAPH;

        float saveY = lastY;
        lastY = drawTextAndBreakLine(cnvs,myPaint,40,lastY, sheetWidth-80,getString(R.string.comeu_bem));
        lastY +=TEXT_PARAGRAPH;
        lastY = drawTextAndBreakLine(cnvs,myPaint,40,lastY, sheetWidth-80,getString(R.string.dormiu_bem));
        lastY +=TEXT_PARAGRAPH;
        lastY = drawTextAndBreakLine(cnvs,myPaint,40,lastY, sheetWidth-80,getString(R.string.fez_necessidades));
        lastY +=TEXT_PARAGRAPH;
        lastY = drawTextAndBreakLine(cnvs,myPaint,40,lastY, sheetWidth-80,getString(R.string.magoou_se));
        lastY +=TEXT_PARAGRAPH;
        drawTextAndBreakLine(cnvs,myPaint,40,lastY, sheetWidth-80,getString(R.string.chorou));
        lastY = drawTextAndBreakLine(cnvs,myPaint,200,saveY,200,finalVeredict(com));
        lastY +=TEXT_PARAGRAPH;
        lastY = drawTextAndBreakLine(cnvs,myPaint,200,lastY,200,finalVeredict(dor));
        lastY +=TEXT_PARAGRAPH;
        lastY = drawTextAndBreakLine(cnvs,myPaint,200,lastY,200,finalVeredict(wc));
        lastY +=TEXT_PARAGRAPH;
        lastY = drawTextAndBreakLine(cnvs,myPaint,200,lastY,200,finalVeredict(cur));
        lastY +=TEXT_PARAGRAPH;
        lastY = drawTextAndBreakLine(cnvs,myPaint,200,lastY,200,finalVeredict2(cur));
        lastY +=TEXT_PARAGRAPH;
        lastY = drawTextAndBreakLine(cnvs,myPaint,40,lastY,sheetWidth-80,getString(R.string.comentario));
        lastY +=TEXT_PARAGRAPH;
        lastY = drawTextAndBreakLine(cnvs,myPaint,40,lastY,sheetWidth-80, not);
        lastY +=TEXT_PARAGRAPH;
        lastY = drawTextAndBreakLine(cnvs,myPaint,40,lastY,sheetWidth-80, "Desejamos que tenha uma continuação de um bom dia,");
        lastY = drawTextAndBreakLine(cnvs,myPaint,40,lastY,sheetWidth-80, "A equipa da TitchersFriend.");
        myPaint.setTextSize(TEXT_MINIATURE);
        drawTextAndBreakLine(cnvs,myPaint,40,sheetHeight-80,sheetWidth-80, "PDF produzido automáticamente pela app TitchersFriend, se ocorrer algum erro na configuração deste documento contacte-nos para o email teachersfriendapp@gmail.com.");
        myDocument.finishPage(myPage1);

        File f = new File(getApplicationContext().getExternalFilesDir(null),"/lastReport.pdf");
        try{
            myDocument.writeTo(new FileOutputStream(f));
        } catch (IOException e) {
            e.printStackTrace();
        }
        myDocument.close();

        c = db.query(DBHelper.TABLE_NAME7,new String[]{DBHelper.COL1_T7},null,null,null,null,null);
        if(!c.moveToFirst()){
            Toast.makeText(getApplicationContext(),"Erro, não conseguimos estabelecer a ligação à base de dados",Toast.LENGTH_SHORT);
            return;
        }
        int id = c.getInt(0);
        String x = null;
        DBHelper.fechando(db,ConferelatorioActivity.this); //para fazer o update de todos os dados
        c.close();
        try {
            x = new Sender(ConferelatorioActivity.this,"106","e="+id+"&c="+contacto+"&d="+data,getApplicationContext().getExternalFilesDir(null)+"/lastReport.pdf").execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Erro ao estabelecer a ligação com a base de dados",Toast.LENGTH_SHORT).show();
        }
        if(x== null){
            Toast.makeText(getApplicationContext(),"Erro na aplicação",Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            JSONObject reader = new JSONObject(x);
            if(reader.getBoolean("success")){
                Toast.makeText(getApplicationContext(),"Enviado",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),x,Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Ocorreu um erro na aplicação",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        db.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        db = dbHelper.getReadableDatabase();
    }
}
