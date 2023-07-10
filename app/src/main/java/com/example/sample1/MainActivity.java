package com.example.sample1;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //Viewで使う変数を初期化（別にここじゃなくてもいいけど）
    TextView txt;
    Button btn1;
    Button btn2;
    Button btn3;

    //NfcAdapterを初期化
    NfcAdapter nfcAdapter;
    //File data = new File("SampleData.txt");
    String str = new String();

    //消すべからず
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UIのマーツをマッピング
        txt = findViewById(R.id.textView);
        btn1 = findViewById(R.id.button);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);

        //nfcAdapter初期化
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        //Reader Mode Offボタンのenabledをfalseに（トグルにするため）
        btn2.setEnabled(false);
        //登録ボタンを隠す(領域を確保しない)
        btn3.setVisibility(View.GONE);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //トグル機能
                btn1.setEnabled(false);
                btn2.setEnabled(true);

                //Redermode On
                nfcAdapter.enableReaderMode(MainActivity.this,new MyReaderCallback(),NfcAdapter.FLAG_READER_NFC_F,null);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //トグル機能
                btn1.setEnabled(true);
                btn2.setEnabled(false);

                //Readermode Off
                nfcAdapter.disableReaderMode(MainActivity.this);

                //表示初期化
                txt.setText("Read ID ...");
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //トグル機能
                //btn1.setEnabled(false);
                //btn2.setEnabled(false);

                //Redermode On
                nfcAdapter.enableReaderMode(MainActivity.this,new MyReaderCallback2(),NfcAdapter.FLAG_READER_NFC_F,null);

                //nfcAdapter.disableReaderMode(MainActivity.this);
                //btn1.setEnabled(true);
            }
        });
    }

    //Callback Class
    private class MyReaderCallback implements NfcAdapter.ReaderCallback{
        @Override
        public void onTagDiscovered(Tag tag){

            Log.d("Hoge","Tag discoverd.");

            //get idm
            byte[] idm = tag.getId();
            int Con = tag.describeContents();
            Integer con = Integer.valueOf(Con);
            String[] techList = tag.getTechList();
            str = con.toString() + "\r\n";
            /*for(String t : techList){
                str += t + "\r\n";
            }
            for(int i = 0; i < techList.length; i++){
                str += (techList[i] + "\r\n");
            }

            //final String idmString = bytesToHexString(idm) + "\r\n" + str + "\r\n" + tag.toString();
            int code = tag.hashCode();
            Integer c = Integer.valueOf(code);
            final String idmString = c.toString();*/
            final String idmString = bytesToHexString(idm);

            //親スレッドのUIを更新するためごにょごにょ
            final Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    txt.setText(idmString);
                }
            });
        }
    }

    private class MyReaderCallback2 implements NfcAdapter.ReaderCallback{
        @Override
        public void onTagDiscovered(Tag tag) {
            Log.d("Hoge","Tag discoverd.");

            byte[] idm = tag.getId();
            final String idmString = bytesToHexString(idm);

            final Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    txt.setText(idmString);
                    //str = idmString;
                }
            });
        }
    }

    //bytes列を16進数文字列に変換（めんどい）
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x ", b);
        }
        return sb.toString().toUpperCase(Locale.getDefault());
    }
}