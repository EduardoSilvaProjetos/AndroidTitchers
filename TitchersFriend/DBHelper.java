package pt.ubi.di.pdm.titchersfriend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.concurrent.ExecutionException;

public class DBHelper extends SQLiteOpenHelper {
    Context context;
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "MyDB2";
    protected static final String TABLE_NAME1 = "educando";
    protected static final String TABLE_NAME2 = "atividade";
    protected static final String TABLE_NAME3 = "alergias";
    protected static final String TABLE_NAME4 = "faltas";
    protected static final String TABLE_NAME5 = "relatorio";
    protected static final String TABLE_NAME6 = "contem";
    protected static final String TABLE_NAME7 = "user";

    protected static final String COL1_T1 = "e_id";
    protected static final String COL2_T1 = "e_nome";
    protected static final String COL3_T1 = "e_idade";
    protected static final String COL4_T1 = "e_morada";
    protected static final String COL5_T1 = "e_sexo";
    protected static final String COL6_T1 = "e_contacto";

    protected static final String COL1_T2 = "a_id";
    protected static final String COL2_T2 = "a_sumario";
    protected static final String COL3_T2 = "a_data";

    protected static final String COL1_T3 = "al_id";
    protected static final String COL2_T3 = "al_nome";

    protected static final String COL1_T4 = "e_id";
    protected static final String COL2_T4 = "a_id";

    protected static final String COL1_T5 = "r_comer";
    protected static final String COL2_T5 = "r_dormir";
    protected static final String COL3_T5 = "r_coment";
    protected static final String COL4_T5 = "r_necessidades";
    protected static final String COL5_T5 = "r_curativos";
    protected static final String COL6_T5 = "e_id";
    protected static final String COL7_T5 = "a_id";

    protected static final String COL1_T6= "al_id";
    protected static final String COL2_T6= "e_id";

    protected static final String COL1_T7= "u_id";
    protected static final String COL2_T7= "u_name";

    private static final String CREATE_EDUCANDO = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME1+"("+COL1_T1+" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+COL2_T1+" VARCHAR(100) NOT NULL,"+COL3_T1+" INTEGER NOT NULL,"+COL4_T1+" VARCHAR(200) NOT NULL,"+COL5_T1+" INTEGER NOT NULL,"+COL6_T1+" VARCHAR(10) NOT NULL)";
    private static final String CREATE_ATIVIDADE = "CREATE TABLE IF NOT EXISTS  "+TABLE_NAME2+"("+COL1_T2+" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+COL2_T2+" VARCHAR(300) NOT NULL,"+COL3_T2+" VARCHAR(300) NOT NULL)";
    private static final String CREATE_ALERGIAS = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME3+"("+COL1_T3+" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+COL2_T3+" VARCHAR(100) NOT NULL)";
    private static final String CREATE_FALTAS = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME4+"("+COL1_T4+" INTEGER NOT NULL,"+COL2_T4+" INTEGER NOT NULL, PRIMARY KEY ("+COL1_T4+","+COL2_T4+"),FOREIGN KEY ("+COL1_T4+") REFERENCES educando("+COL1_T4+"),FOREIGN KEY ("+COL2_T4+") REFERENCES atividade("+COL2_T4+"))";
    private static final String CREATE_RELATORIO = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME5+"("+COL1_T5+" INTEGER NOT NULL,"+COL2_T5+" INTEGER NOT NULL,"+COL3_T5+" VARCHAR(500) NOT NULL,"+COL4_T5+" INTEGER NOT NULL,"+COL5_T5+" INTEGER NOT NULL,"+COL6_T5+" INTEGER NOT NULL,"+COL7_T5+" INTEGER NOT NULL,PRIMARY KEY ("+COL6_T5+","+COL7_T5+"),FOREIGN KEY ("+COL6_T5+") REFERENCES educando("+COL6_T5+"),FOREIGN KEY ("+COL7_T5+") REFERENCES atividade("+COL7_T5+"))";
    private static final String CREATE_CONTEM = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME6+"("+COL1_T6+" INTEGER NOT NULL,"+COL2_T6+" INTEGER NOT NULL, PRIMARY KEY ("+COL1_T6+","+COL2_T6+"),FOREIGN KEY ("+COL1_T6+") REFERENCES alergias("+COL1_T6+"),FOREIGN KEY ("+COL2_T6+") REFERENCES educando("+COL2_T6+"))";
    private static final String CREATE_USER = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME7+"("+COL1_T7+" INTEGER NOT NULL PRIMARY KEY,"+COL2_T7+" VARCHAR(100) NOT NULL)";
    public DBHelper(@Nullable Context context) {

        super(context, DB_NAME, null, DB_VERSION);
        this.context=context;
    }
    public void delete(){
        context.deleteDatabase(DB_NAME);
    }

    public void onDelete(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME1+";");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME2+";");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME3+";");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME4+";");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME5+";");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME6+";");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME7+";");

        db.execSQL(CREATE_EDUCANDO);
        db.execSQL(CREATE_ATIVIDADE);
        db.execSQL(CREATE_ALERGIAS);
        db.execSQL(CREATE_FALTAS);
        db.execSQL(CREATE_RELATORIO);
        db.execSQL(CREATE_CONTEM);
        db.execSQL(CREATE_USER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EDUCANDO);
        db.execSQL(CREATE_ATIVIDADE);
        db.execSQL(CREATE_ALERGIAS);
        db.execSQL(CREATE_FALTAS);
        db.execSQL(CREATE_RELATORIO);
        db.execSQL(CREATE_CONTEM);
        db.execSQL(CREATE_USER);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE "+TABLE_NAME1+";");
        db.execSQL("DROP TABLE "+TABLE_NAME2+";");
        db.execSQL("DROP TABLE "+TABLE_NAME3+";");
        db.execSQL("DROP TABLE "+TABLE_NAME4+";");
        db.execSQL("DROP TABLE "+TABLE_NAME5+";");
        db.execSQL("DROP TABLE "+TABLE_NAME6+";");
        db.execSQL("DROP TABLE "+TABLE_NAME7+";");

        db.execSQL(CREATE_EDUCANDO);
        db.execSQL(CREATE_ATIVIDADE);
        db.execSQL(CREATE_ALERGIAS);
        db.execSQL(CREATE_FALTAS);
        db.execSQL(CREATE_RELATORIO);
        db.execSQL(CREATE_CONTEM);
        db.execSQL(CREATE_USER);

    }

    public static void fechando(SQLiteDatabase base, Context c) {
        String x = "false";
        String total = "";
        String user = "";
        String id = "";
        int count = 0;
        Cursor cursor = base.query(TABLE_NAME7, new String[]{"*"}, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            user = cursor.getString(cursor.getColumnIndex(COL2_T7));
            id = cursor.getString(cursor.getColumnIndex(COL1_T7));
        }
        total = total + "user=" + user;
                cursor = base.query(TABLE_NAME1, new String[]{"*"}, null, null, null, null, null);

        if(cursor.getCount()>0){
            total = total + "&";
        }
        while (cursor.moveToNext()) {
            String c1 = cursor.getString(cursor.getColumnIndex(COL1_T1));
            String c2 = cursor.getString(cursor.getColumnIndex(COL2_T1));
            String c3 = cursor.getString(cursor.getColumnIndex(COL3_T1));
            String c4 = cursor.getString(cursor.getColumnIndex(COL4_T1));
            String c5 = cursor.getString(cursor.getColumnIndex(COL5_T1));
            String c6 = cursor.getString(cursor.getColumnIndex(COL6_T1));
            if (count == 0)
                total += "ed" + "=" + c1 + "," + c2 + "," + c3 + "," + c4 + "," + c5 + "," + c6;
            if (count != 0)
                total = total + ";" + c1 + "," + c2 + "," + c3 + "," + c4 + "," + c5 + "," + c6;
            count++;
        }
        count = 0;
        cursor = base.query(TABLE_NAME2, new String[]{"*"}, null, null, null, null, null);
        if(cursor.getCount()>0){
            total = total + "&";
        }
        while (cursor.moveToNext()) {

            String c1 = cursor.getString(cursor.getColumnIndex(COL2_T2));
            String c2 = cursor.getString(cursor.getColumnIndex(COL1_T2));
            String c3 = cursor.getString(cursor.getColumnIndex(COL3_T2));
            if (count == 0)
                total = total + "at" + "=" + c1 + "," + c2 + "," + c3;
            if (count != 0)
                total = total + ";" + c1 + "," + c2 + "," + c3;
            count++;
        }
        count = 0;
        cursor = base.query(TABLE_NAME3, new String[]{"*"}, null, null, null, null, null);
        if(cursor.getCount()>0){
            total = total + "&";
        }
        while (cursor.moveToNext()) {

            String c1 = cursor.getString(cursor.getColumnIndex(COL1_T3));
            String c2 = cursor.getString(cursor.getColumnIndex(COL2_T3));

            if (count == 0)
                total = total + "al" + "=" + c1 + "," + c2;
            if (count != 0)
                total = total + ";" + c1 + "," + c2;
            count++;
        }
        count = 0;
        cursor = base.query(TABLE_NAME4, new String[]{"*"}, null, null, null, null, null);
        if(cursor.getCount()>0){
            total = total + "&";
        }
        while (cursor.moveToNext()) {

            String c1 = cursor.getString(cursor.getColumnIndex(COL1_T4));
            String c2 = cursor.getString(cursor.getColumnIndex(COL2_T4));

            if (count == 0)
                total = total + "fa" + "=" + c1 + "," + c2;
            if (count != 0)
                total = total + ";" + c1 + "," + c2;
            count++;
        }
        count = 0;
        cursor = base.query(TABLE_NAME5, new String[]{"*"}, null, null, null, null, null);
        if(cursor.getCount()>0){
            total = total + "&";
        }
        while (cursor.moveToNext()) {

            String c1 = cursor.getString(cursor.getColumnIndex(COL1_T5));
            String c2 = cursor.getString(cursor.getColumnIndex(COL2_T5));
            String c3 = cursor.getString(cursor.getColumnIndex(COL3_T5));
            String c4 = cursor.getString(cursor.getColumnIndex(COL4_T5));
            String c5 = cursor.getString(cursor.getColumnIndex(COL5_T5));
            String c6 = cursor.getString(cursor.getColumnIndex(COL6_T5));
            String c7 = cursor.getString(cursor.getColumnIndex(COL7_T5));
            if (count == 0)
                total = total + "rel" + "=" + c1 + "," + c2 + "," + c3 + "," + c4 + "," + c5 + "," + c6 + "," + c7;
            if (count != 0)
                total = total + ";" + c1 + "," + c2 + "," + c3 + "," + c4 + "," + c5 + "," + c6 + "," + c7;
            count++;
        }
        count = 0;
        cursor = base.query(TABLE_NAME6, new String[]{"*"}, null, null, null, null, null);
        if(cursor.getCount()>0){
            total = total + "&";
        }
        while (cursor.moveToNext()) {
            String c1 = cursor.getString(cursor.getColumnIndex(COL1_T6));
            String c2 = cursor.getString(cursor.getColumnIndex(COL2_T6));

            if (count == 0)
                total = total + "cont" + "=" + c1 + "," + c2;
            if (count != 0)
                total = total + ";" + c1 + "," + c2;
            count++;
        }

        try {

            x = new Sender(c, "104", total, null).execute().get();
            //Toast.makeText(c,x,Toast.LENGTH_SHORT).show();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

}
