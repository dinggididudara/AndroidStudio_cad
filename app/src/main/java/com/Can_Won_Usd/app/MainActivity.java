/**
 * @author Soomin aka dinggididudara
 * @version 1.0
 */
package com.Can_Won_Usd.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
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
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.text.DecimalFormat;

import static java.lang.Double.parseDouble;
import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity{
    EditText imeCad; //main page 에서 textview 3개
    EditText imeUsd;
    EditText imeWon;

    ImageView c;
    ImageView u;

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

    DecimalFormat df; //formatter for money

    Document cadDoc;
    Document usdDoc;
    Element cadFromWebsiteElement;
    Element usdFromWebsiteElement;

    Button cadCalculateButton;
    Button wonCalculateButton;
    Button usdCalculateButton;
    Button clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imeCad = (EditText) findViewById(R.id.cadTextView);
        imeUsd = (EditText) findViewById(R.id.usdTextView);
        imeWon = (EditText) findViewById(R.id.wonTextView);

        c = (ImageView) findViewById(R.id.c);
        u = (ImageView) findViewById(R.id.u);

        cadCalculateButton = findViewById(R.id.cadCalculate);
        wonCalculateButton = findViewById(R.id.wonCalculate);
        usdCalculateButton = findViewById(R.id.usdCalculate);
        clear = findViewById(R.id.clearButton);

        df = new DecimalFormat("###,###,###,###,###.##"); //formatter for money



        JsoupAsyncTask j = new JsoupAsyncTask();
        j.execute();

        /**
         * Google banner advertisement
         * - adView initialize and set
         */
//        AdView ad;
//        MobileAds.initialize(this, new OnInitializationCompleteListener(){
//            @Override
//            public void onInitializationComplete(InitializationStatus InitializationStatus){
//            }
//        });
//        ad = (AdView) findViewById(R.id.ad);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        ad.loadAd(adRequest);
//        AdView adView = new AdView(this);
//        adView.setAdSize(AdSize.BANNER);
//        adView.setAdUnitId("\n" + "ca-app-pub-5624531041743764/2799586139");
        /////////adview end-------------------------------------------------------------------------

        /**
         * click image 'cad' -> show the currency today (Toast)
         * click image 'usd' -> show the currency today (Toast)
         */
        c.setOnClickListener(new View.OnClickListener(){ //when click image of canada
            @Override
            public void onClick(View view){
                Toast t = Toast.makeText(getApplicationContext(), cadFromWebsite2DoubleType + " $C", Toast.LENGTH_SHORT);
                t.setGravity(Gravity.TOP | Gravity.LEFT, 138,450);
                t.show();
            }
        });
        u.setOnClickListener(new View.OnClickListener(){ //when click image of usa
            @Override
            public void onClick(View view){
                Toast t = Toast.makeText(getApplicationContext(), usdFromWebsite2DoubleType + " $U", Toast.LENGTH_SHORT);
                t.setGravity(Gravity.TOP | Gravity.LEFT, 138, 1500);
                t.show();
            }
        });
        /**
         * click clear button -> all set null
         */
        clear.setOnClickListener(new View.OnClickListener() {//click clear button
            @Override
            public void onClick(View v) {
                imeCad.setText(null);
                imeWon.setText(null);
                imeUsd.setText(null);
            }
        });
        /**
         *  if click textview and then calculate button
         */
        cadCalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ifCadEntered();
            }
        });
        wonCalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ifWonEntered();
            }
        });
        usdCalculateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                new ifUsdEntered();
            }
        });
        /**
         * click keyboard enter -> click calculate button
         */
        imeCad.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) { //click keyboard enter
                    new ifCadEntered();
                    return true;
                }
                return false;
            }
        });
        imeUsd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) { //click keyboard enter
                    new ifUsdEntered();
                    return true;
                }
                return false;
            }
        });
        imeWon.setOnEditorActionListener(new TextView.OnEditorActionListener() {
           @Override
           public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
              if (actionId == EditorInfo.IME_ACTION_SEND) { //click keyboard enter
                  new ifWonEntered();
                  return true;
              }
              return false;
           }
        });
    }//onCreate end

    /**
     * JsoupAsyncTask -> parsing website
     *
     */
     public class JsoupAsyncTask extends AsyncTask<Double, String[], Double[]> {//parsing from website

        final String cad = "https://finance.naver.com/marketindex/exchangeDetail.nhn?marketindexCd=FX_CADKRW";
        final String usd = "https://finance.naver.com/marketindex/exchangeDetail.nhn?marketindexCd=FX_USDKRW";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        /**
         *
         * @param resultArr : double type array
         * @return resultArr : currency from website (cad and usd)
         */
        @Override
        protected Double[] doInBackground(Double... resultArr) {
            try {
                resultArr = new Double[2];
                cadDoc = Jsoup.connect(cad).get();               //website 에서 숫자 가져오기(cad)
                cadFromWebsiteElement = cadDoc.select(".tbl_calculator td").get(0);
                cadFromWebsite2=cadFromWebsiteElement.text().replaceAll(",", "");
                cadFromWebsite2DoubleType = parseDouble(cadFromWebsite2.trim());

                usdDoc = Jsoup.connect(usd).get();              //website 에서 숫자 가져오기 (usd)
                usdFromWebsiteElement = usdDoc.select(".tbl_calculator td").get(0);
                usdFromWebsite2=usdFromWebsiteElement.text().replaceAll(",", "");
                usdFromWebsite2DoubleType = parseDouble(usdFromWebsite2.trim());

                resultArr[0] = cadFromWebsite2DoubleType;
                resultArr[1] = usdFromWebsite2DoubleType;

            } catch (Exception e) {
                e.printStackTrace();
//                Toast.makeText(getApplicationContext(),"잠시만요!", Toast.LENGTH_SHORT).show();
            }
            return resultArr;
        }
        protected void onPostExecute(Double[] resultArr){
            super.onPostExecute(resultArr);
        }
    } //JsoupAsyncTask end

    private class ifCadEntered extends JsoupAsyncTask {
         ifCadEntered(){
             imeWon.setText("");
             imeUsd.setText("");
             beforeCadTextViewDoubleType = imeCad.getText().toString();//textview 에서 값 가져오기
             cadDoubleType = Double.parseDouble(beforeCadTextViewDoubleType); //가져온 값을 double type 으로 바꿔주기

             calculatedWon = Math.round(cadDoubleType * cadFromWebsite2DoubleType * 100) / 100.00;//계산하기
             calculatedUsd = Math.round(cadDoubleType * usdFromWebsite2DoubleType / cadFromWebsite2DoubleType * 100) / 100.00;

             imeWon.setText(df.format(calculatedWon) + " ₩");
             imeUsd.setText(df.format(calculatedUsd) + " $U");
         }
    } //ifCadEntered ends
    private class ifWonEntered extends JsoupAsyncTask {
        ifWonEntered(){
            imeCad.setText("");
            imeUsd.setText("");
            beforeWonTextViewDoubleType = imeWon.getText().toString();//textview 에서 값 가져오기
            wonDoubleType = Double.parseDouble(beforeWonTextViewDoubleType); //가져온 값을 double type 으로 바꿔주기

            calculatedCad = Math.round(wonDoubleType / cadFromWebsite2DoubleType * 100) / 100.00; //계산하기
            calculatedUsd = Math.round(wonDoubleType / usdFromWebsite2DoubleType * 100) / 100.00;

            imeCad.setText(df.format(calculatedCad) + " $C");
            imeUsd.setText(df.format(calculatedUsd) + " $U");

        }
    } //ifWonEntered ends
    private class ifUsdEntered extends JsoupAsyncTask {
        ifUsdEntered(){
            imeCad.setText("");
            imeWon.setText("");
            beforeUsdTextViewDoubleType = imeUsd.getText().toString(); //textview 에서 값 가져오기
            usdDoubleType = Double.parseDouble(beforeUsdTextViewDoubleType); //가져온 값을 double type 으로 바꿔주기

            calculatedCad = Math.round(usdDoubleType / cadDoubleType * 100) / 100.00; //계산하기
            calculatedWon = Math.round(usdDoubleType * cadFromWebsite2DoubleType / usdFromWebsite2DoubleType * 100) / 100.00;

            imeWon.setText(df.format(calculatedWon) + " ₩");
            imeCad.setText(df.format(calculatedUsd) + " $U");
        }
    } //ifUsdEntered ends

}//MainActivity ends