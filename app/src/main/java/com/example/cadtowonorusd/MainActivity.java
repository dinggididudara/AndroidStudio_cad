/**
 * @author Soomin aka dinggididudara
 * @version 1.0
 */
package com.example.cadtowonorusd;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.DecimalFormat;

import static java.lang.Double.parseDouble;
import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {
    EditText imeCad; //main page 에서 textview 3개
    EditText imeUsd;
    EditText imeWon;

    ImageView c;
    ImageView u;
    ImageView w;

    String cadFromWebsite2;
    String usdFromWebsite2;//태그가 애매해서 하나 더 declare 해야함
    double cadFromWebsite2DoubleType;
    double usdFromWebsite2DoubleType;//website 에서 가져온 값 double type 으로 전환
    String beforeCadTextViewDoubleType;
    String beforeWonTextViewDoubleType;
    String beforeUsdTextViewDoubleType;//웹사이트에서 가져온 값 string 으로 전환
    double calculatedCad;
    double calculatedWon;
    double calculatedUsd; //환율 계산한 값
    double cadDoubleType;
    double wonDoubleType;
    double usdDoubleType;//string 으로 전환한 값 double 로 넣기

    static DecimalFormat df = new DecimalFormat("###,###,###,###,###.##");

    Document cadDoc;
    Document usdDoc;
    Element cadFromWebsiteElement;
    Element usdFromWebsiteElement;

    Button calculate;
    Button clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
        * adView initialize and set
         */
        AdView ad;
        MobileAds.initialize(this, new OnInitializationCompleteListener(){
            @Override
            public void onInitializationComplete(InitializationStatus InitializationStatus){
            }
        });
        ad = findViewById(R.id.ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad.loadAd(adRequest);
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("\n" + "ca-app-pub-3940256099942544/6300978111");//test id, change later

        imeCad = findViewById(R.id.cadTextView);
        imeUsd = findViewById(R.id.usdTextView);
        imeWon = findViewById(R.id.wonTextView);

        c = findViewById(R.id.c);
        u = findViewById(R.id.u);

        /**
         * click image 'cad' -> show the currency today
         * click image 'usd' -> show the currency today
         */
        c.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view){
                new JsoupAsyncTask2().execute() ;
                Toast.makeText(getApplicationContext(), String.valueOf(cadFromWebsite2DoubleType), Toast.LENGTH_SHORT).show();
            }
        });
        u.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view){
                new JsoupAsyncTask2().execute();
                Toast.makeText(getApplicationContext(), String.valueOf(usdFromWebsite2DoubleType), Toast.LENGTH_SHORT).show();
            }
        });



        /**
         * click calculate button -> Jsoup execute
         */
        calculate = (Button) findViewById(R.id.calculateButton);
        calculate.setOnClickListener(new View.OnClickListener() {//click calculate button
            @Override
            public void onClick(View v) {
                new JsoupAsyncTask().execute();
            }
        });//calculate button onClick end
        /**
         * click clear button
         */
        clear = (Button) findViewById(R.id.clearButton);
        clear.setOnClickListener(new View.OnClickListener() {//click clear button
            @Override
            public void onClick(View v) {
                imeCad.setText(null);
                imeWon.setText(null);
                imeUsd.setText(null);
            }
        });
        /**
         * click keyboard enter -> click calculate button
         */
        imeCad.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    calculate.callOnClick();
                    return true;
                }
                return false;
            }
        });
        imeUsd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    calculate.callOnClick();
                    return true;
                }
                return false;
            }
        });
        imeWon.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    calculate.callOnClick();
                    return true;
                }
                return false;
            }
        });
    }//onCreate end
    public class JsoupAsyncTask2 extends AsyncTask<Double, Double[], Double[]> {
        @Override
        protected Double[] doInBackground(Double... voids) {
            final String cad = "https://finance.naver.com/marketindex/exchangeDetail.nhn?marketindexCd=FX_CADKRW";
            final String usd = "https://finance.naver.com/marketindex/exchangeDetail.nhn?marketindexCd=FX_USDKRW";

            try {
                cadDoc = Jsoup.connect(cad).get();//website 에서 숫자 가져오기(cad)
                cadFromWebsiteElement = cadDoc.select(".tbl_calculator td").get(0);
                cadFromWebsite2=cadFromWebsiteElement.text().replaceAll(",", "");
                cadFromWebsite2DoubleType = parseDouble(cadFromWebsite2.trim());

                usdDoc = Jsoup.connect(usd).get();//website 에서 숫자 가져오기 (usd)
                usdFromWebsiteElement = usdDoc.select(".tbl_calculator td").get(0);
                usdFromWebsite2=usdFromWebsiteElement.text().replaceAll(",", "");
                usdFromWebsite2DoubleType = parseDouble(usdFromWebsite2.trim());

                voids[0] = cadFromWebsite2DoubleType;
                voids[1]= usdFromWebsite2DoubleType;

             } catch(IOException e) {
                  e.printStackTrace();
             }
            return voids;
    }
}
    /**
     *
     *
     */
    public class JsoupAsyncTask extends AsyncTask<String, String[], String[]> {
        public int i;
        public int CheckNull() {
            if(imeCad.getText() != null && imeWon.getText() == null && imeUsd.getText() == null){
              return i=0;
            }else if (imeCad.getText() == null && imeWon.getText() != null && imeUsd.getText() == null) {
              return i=1;
            }else if (imeCad.getText() == null && imeWon.getText() == null && imeUsd.getText() != null) {
              return i=2;
            }else if (imeCad.getText() == null && imeWon.getText() == null && imeUsd.getText() == null) {
                Toast.makeText(getApplicationContext(), "아무값이나 입력하세요!", Toast.LENGTH_SHORT).show();
            }
            return i;
        }

        final String cad = "https://finance.naver.com/marketindex/exchangeDetail.nhn?marketindexCd=FX_CADKRW";
        final String usd = "https://finance.naver.com/marketindex/exchangeDetail.nhn?marketindexCd=FX_USDKRW";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(String... resultArr) {
            try {
                cadDoc = Jsoup.connect(cad).get();//website 에서 숫자 가져오기(cad)
                cadFromWebsiteElement = cadDoc.select(".tbl_calculator td").get(0);
                cadFromWebsite2=cadFromWebsiteElement.text().replaceAll(",", "");
                cadFromWebsite2DoubleType = parseDouble(cadFromWebsite2.trim());

                usdDoc = Jsoup.connect(usd).get();//website 에서 숫자 가져오기 (usd)
                usdFromWebsiteElement = usdDoc.select(".tbl_calculator td").get(0);
                usdFromWebsite2=usdFromWebsiteElement.text().replaceAll(",", "");
                usdFromWebsite2DoubleType = parseDouble(usdFromWebsite2.trim());

                switch (i) {//cad 에 값을 넣었을때
                    case 0:
                        beforeCadTextViewDoubleType = imeCad.getText().toString();//textview 에서 값 가져오기
                        cadDoubleType = Double.parseDouble(beforeCadTextViewDoubleType); //가져온 값을 double type 으로 바꿔주기

                        calculatedWon = Math.round(cadDoubleType * cadFromWebsite2DoubleType * 100) / 100.00;//계산하기
                        calculatedUsd = Math.round(cadDoubleType * usdFromWebsite2DoubleType / cadFromWebsite2DoubleType * 100) / 100.00;


                        imeWon.setText(df.format(calculatedWon) + " ₩");
                        imeUsd.setText(df.format(calculatedUsd) + " $");

//                        resultArr[0] = df.format(cadDoubleType);//result 에 값 넣기
//                        resultArr[1] = String.valueOf(calculatedWon);
//                        resultArr[2] = String.valueOf(calculatedUsd);
                        break;

                    case 1://won 에 값 넣었을때
                        beforeWonTextViewDoubleType = imeWon.getText().toString();//textview 에서 값 가져오기
                        wonDoubleType = Double.parseDouble(beforeWonTextViewDoubleType); //가져온 값을 double type 으로 바꿔주기

                        calculatedCad = Math.round(wonDoubleType / cadFromWebsite2DoubleType * 100) / 100.00; //계산하기
                        calculatedUsd = Math.round(wonDoubleType / usdFromWebsite2DoubleType * 100) / 100.00;

                        imeCad.setText(df.format(calculatedCad) + " ₩");
                        imeUsd.setText(df.format(calculatedUsd) + " $");

//                        resultArr[0] = df.format(wonDoubleType);//result 에 값 넣기
//                        resultArr[1] = df.format(calculatedCad);
//                        resultArr[2] = df.format(calculatedUsd);
                        break;

                    case 2://usd 에 값 넣었을때
                        beforeUsdTextViewDoubleType = imeUsd.getText().toString(); //textview 에서 값 가져오기
                        usdDoubleType = Double.parseDouble(beforeUsdTextViewDoubleType); //가져온 값을 double type 으로 바꿔주기

                        calculatedCad = Math.round(usdDoubleType / cadDoubleType * 100) / 100.00; //계산하기
                        calculatedWon = Math.round(usdDoubleType * cadFromWebsite2DoubleType / usdFromWebsite2DoubleType * 100) / 100.00;

                        imeWon.setText(df.format(calculatedWon));
                        imeCad.setText(df.format(calculatedUsd));

//                        resultArr[0] = df.format(calculatedCad);//result 에 값 넣기
//                        resultArr[1] = df.format(calculatedWon);
//                        resultArr[2] = df.format(usdDoubleType);
                        break;

                    default:
                        Toast.makeText(getApplicationContext(), "아무값이나 입력하세요!", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultArr;
        }
        @Override
        protected void onPostExecute(String[] resultArr) {
            super.onPostExecute(resultArr);
        }
    }
}