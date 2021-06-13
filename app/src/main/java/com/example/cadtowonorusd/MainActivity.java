package com.example.cadtowonorusd;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    TextView cadTextView;
    TextView wonTextView;
    TextView usdTextView; //main page 에서 textview 3개

    static final String TAG = "onPreExecute()";//로그찍기

    String cadFromWebsite2;
    String usdFromWebsite2;//태그가 애매해서 하나 더 declare 해야함
    double cadFromWebsite2DoubleType;
    double usdFromWebsite2DoubleType;//website엥서 가져온 값 double type 으로 전환
    String beforeCadTextViewDoubleType;
    String beforeWonTextViewDoubleType;
    String beforeUsdTextViewDoubleType;//웹사이트에서 가져온 값 string으로 전환
    double calculatedCad;
    double calculatedWon;
    double calculatedUsd; //환율 계산한 값
    double cadDoubleType;
    double wonDoubleType;
    double usdDoubleType;//string 으로 전환한 값 double 로 넣기

    String[] resultArr;

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

        Log.v(TAG, "이건 성공");
        Log.e(TAG, "이건 실패ㅠ");

        cadTextView = findViewById(R.id.cadTextView);
        wonTextView = findViewById(R.id.wonTextView);
        usdTextView = findViewById(R.id.usdTextView);

        calculate = findViewById(R.id.calculate);
        clear = findViewById(R.id.clearButton);

        calculate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new JsoupAsyncTask().execute();
            }
        });//calculate button onClick end
        clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cadTextView.setText(null);
                wonTextView.setText(null);
                usdTextView.setText(null);
            }
        });
    }//onCreate end

    public class JsoupAsyncTask extends AsyncTask<String, String[], String[]> {
        final String cad = "https://finance.naver.com/marketindex/exchangeDetail.nhn?marketindexCd=FX_CADKRW";
        final String usd = "https://finance.naver.com/marketindex/exchangeDetail.nhn?marketindexCd=FX_USDKRW";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(String... resultArr) {
            try {
                cadDoc = Jsoup.connect(cad).get();
                cadFromWebsiteElement = cadDoc.select(".tbl_calculator td").get(0);
                cadFromWebsite2=cadFromWebsiteElement.text().replace(",", "");;
                cadFromWebsite2DoubleType = Double.parseDouble(cadFromWebsite2);

                usdDoc = Jsoup.connect(usd).get();
                usdFromWebsiteElement = usdDoc.select(".tbl_calculator td").get(0);
                usdFromWebsite2=usdFromWebsiteElement.text().replace(",", "");;
                usdFromWebsite2DoubleType = Double.parseDouble(usdFromWebsite2);

                if (cadTextView.getText() != null && wonTextView.getText() == null && usdTextView.getText() == null) {//cad에 값을 넣었을때
                    beforeCadTextViewDoubleType = cadTextView.getText().toString();//textview에서 값 가져오기
                    cadDoubleType = Double.parseDouble(beforeCadTextViewDoubleType); //가져온 값을 double type 으로 바꿔주기

                    calculatedWon = Math.round(cadDoubleType * cadFromWebsite2DoubleType * 100) / 100.00;//계산하기
                    calculatedUsd = Math.round(cadDoubleType * usdFromWebsite2DoubleType / cadFromWebsite2DoubleType * 100) / 100.00;

                    resultArr[0] = String.valueOf(cadTextView.getText());//result 에 값 넣기
                    resultArr[1] = String.valueOf(calculatedWon);
                    resultArr[2] = String.valueOf(calculatedUsd);

                    cadTextView.setText(resultArr[0] + "$");
                    wonTextView.setText(resultArr[1] + "₩");
                    usdTextView.setText(resultArr[2] + "$");

                } else if (cadTextView.getText() == null && wonTextView.getText() != null && usdTextView.getText() == null) {//won에 값 넣었을때
                    beforeWonTextViewDoubleType = wonTextView.getText().toString();//textview 에서 값 가져오기
                    wonDoubleType = Double.parseDouble(beforeWonTextViewDoubleType); //가져온 값을 double type 으로 바꿔주기

                    calculatedCad = Math.round(wonDoubleType / cadFromWebsite2DoubleType * 100) / 100.00; //계산하기
                    calculatedUsd = Math.round(wonDoubleType / usdFromWebsite2DoubleType * 100) / 100.00;

                    resultArr[0] = String.valueOf(calculatedCad);//result 에 값 넣기
                    resultArr[1] = String.valueOf(wonTextView.getText());
                    resultArr[2] = String.valueOf(calculatedUsd);

                    cadTextView.setText(resultArr[0] + "$");
                    wonTextView.setText(resultArr[1] + "₩");
                    usdTextView.setText(resultArr[2] + "$");

                } else if (cadTextView.getText() == null && wonTextView.getText() == null && usdTextView.getText() != null) {//usd에 값 넣었을때
                    beforeUsdTextViewDoubleType = usdTextView.getText().toString(); //textview 에서 값 가져오기
                    usdDoubleType = Double.parseDouble(beforeUsdTextViewDoubleType); //가져온 값을 double type 으로 바꿔주기

                    calculatedCad = Math.round(usdDoubleType / cadDoubleType * 100) / 100.00; //계산하기
                    calculatedWon = Math.round(usdDoubleType * cadFromWebsite2DoubleType / usdFromWebsite2DoubleType * 100) / 100.00;

                    resultArr[0] = String.valueOf(calculatedCad);//result 에 값 넣기
                    resultArr[1] = String.valueOf(calculatedWon);
                    resultArr[2] = String.valueOf(usdTextView.getText());

                    cadTextView.setText(resultArr[0] + "$");
                    wonTextView.setText(resultArr[1] + "₩");
                    usdTextView.setText(resultArr[2] + "$");

                } else if (cadTextView.getText() == null && wonTextView.getText() == null && usdTextView.getText() == null) {//아무값도 없을때
                    Toast.makeText(getApplicationContext(), "아무값이나 입력하세요!", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultArr;
//            cadTextView.setText(resultArr[0]);
//            wonTextView.setText(resultArr[1]);
//            usdTextView.setText(resultArr[2]);
        }
        @Override
        protected void onPostExecute(String[] resultArr) {
            super.onPostExecute(resultArr);
//            super.onPostExecute(cadTextView.setText(resultArr[0] + "$"));
//            super.onPostExecute(wonTextView.setText(resultArr[1] + "₩"));
//            super.onPostExecute(usdTextView.setText(resultArr[2] + "$"));
        }
    }
}