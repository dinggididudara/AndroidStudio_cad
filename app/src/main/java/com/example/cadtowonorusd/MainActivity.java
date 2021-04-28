package com.example.cadtowonorusd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;
import android.os.Handler;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity{
    long now;
    Date date;
    TextView timedate;

    TextView cadTextView;
    TextView wonTextView;
    TextView usdTextView; //main page 에서 textview 3개
    double calculatedCad;
    double calculatedWon;
    double calculatedUsd; //환율 계산한 값
    String beforeCadTextViewDoubleType;
    String beforeWonTextViewDoubleType;
    String beforeUsdTextViewDoubleType;//웹사이트에서 가져온 값 string으로 전환
    double cadDoubleType;
    double wonDoubleType;
    double usdDoubleType;//string 으로 전환한 값 double 로 넣기
    String resultCad;
    String resultWon;
    String resultUsd;//결과 저장할 문자열 변수
    String cadFromWebsitee;
    String usdFromWebsitee;//태그가 애매해서 하나 더 declare 해야함


    Document cadDoc=null;
    Document wonDoc=null;
    Document usdDoc=null;
    Elements cadFromWebsite;
    Elements wonFromWebsite;
    Elements usdFromWebsite;

    Button calculate;
    Button clear;
    ImageView imageView;
    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    Animation animation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calculate = (Button) findViewById(R.id.refresh);
        timedate = (TextView) findViewById(R.id.timeDate);

        cadTextView = (TextView) findViewById(R.id.cad);
        wonTextView = (TextView) findViewById(R.id.won);
        usdTextView = (TextView) findViewById(R.id.usd);

        //animation---------------------------------------------------------------------------------
        imageView = (ImageView) findViewById(R.id.refreshimage);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        imageView.startAnimation(animation);//------------------------------------------------------

        final Bundle bundle = new Bundle();

        calculate.setOnClickListener(new View.OnClickListener() {

            private String getTime() {
                now = System.currentTimeMillis();
                date = new Date(now);
                return format.format(date);
            }
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.refresh:
                        timedate.setText(getTime());
                        break;
                    default:
                        break;
                }
            JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
            jsoupAsyncTask.execute();
             }

             //clear button 할 거 만들기
        });//click 했을 때 할 거 end
    }//onCreate end

    private class JsoupAsyncTask extends AsyncTask<Void,Void,Void> {
        private String cad = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=%EC%BA%90%EB%82%98%EB%8B%A4+%ED%99%98%EC%9C%A8";
        private String won = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=%EC%BA%90%EB%82%98%EB%8B%A4+%ED%99%98%EC%9C%A8";
        private String usd = "https://search.naver.com/search.naver?sm=tab_hty.top&where=nexearch&query=%EB%AF%B8%EA%B5%AD+%ED%99%98%EC%9C%A8&oquery=%EC%BA%90%EB%82%98%EB%8B%A4+%ED%99%98%EC%9C%A8&tqi=h4NFvwprvxZss6TOgG0ssssst5s-349063";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                cadDoc = Jsoup.connect(cad).get();
                cadFromWebsite = cadDoc.select("#ds_to_money");
                cadFromWebsitee = cadFromWebsite.attr("value");

//                wonDoc = Jsoup.connect(won).get();
//                wonFromWebsite = wonDoc.select(""); //won 은 항상 1 인가..??

                usdDoc = Jsoup.connect(usd).get();
                usdFromWebsite = usdDoc.select(".ds_to_money");
                usdFromWebsitee = usdFromWebsite.attr("value");


                if (cadTextView != null) {
                    wonDoubleType=Double.parseDouble(wonFromWebsite.toString());
                    usdDoubleType=Double.parseDouble(usdFromWebsitee.toString());

                    beforeCadTextViewDoubleType=cadTextView.getText().toString();//textview에서 값 가져오기
                    cadDoubleType=Double.parseDouble(beforeCadTextViewDoubleType); //가져온 값을 double type 으로 바꿔주기

                    calculatedWon=Math.round(cadDoubleType/wonDoubleType*100)/100.0;//계산하기
                    calculatedUsd=Math.round(cadDoubleType/usdDoubleType*100)/100.0;

                    resultCad=String.valueOf(cadTextView);
                    resultWon=String.valueOf(calculatedWon);
                    resultUsd=String.valueOf(calculatedUsd);
                } else if (wonTextView != null) {
                    cadDoubleType=Double.parseDouble(cadFromWebsite.toString());
                    usdDoubleType=Double.parseDouble(usdFromWebsitee.toString());

                    beforeWonTextViewDoubleType = wonTextView.getText().toString();//textview 에서 값 가져오기
                    wonDoubleType=Double.parseDouble(beforeWonTextViewDoubleType); //가져온 값을 double type 으로 바꿔주기

                    calculatedCad = Math.round(wonDoubleType*cadDoubleType*100)/100.0; //계산하기
                    calculatedUsd = Math.round(wonDoubleType*usdDoubleType*100)/100.0;

                    resultCad=String.valueOf(calculatedCad);//result 에 값 넣기
                    resultWon=String.valueOf(wonTextView);
                    resultUsd=String.valueOf(calculatedUsd);




                } else if (usdTextView != null) {
                    cadDoubleType=Double.parseDouble(cadFromWebsite.toString());
                    wonDoubleType=Double.parseDouble(wonFromWebsite.toString()); //won 값??

                    beforeUsdTextViewDoubleType = usdTextView.getText().toString(); //textview 에서 값 가져오기
                    usdDoubleType=Double.parseDouble(beforeUsdTextViewDoubleType); //가져온 값을 double type 으로 바꿔주기

                    calculatedCad = Math.round(usdDoubleType/cadDoubleType*100)/100.0; //계산하기
                    calculatedWon = Math.round(usdDoubleType/wonDoubleType*100)/100.0;

                    resultCad=String.valueOf(calculatedCad);//result 에 값 넣기
                    resultWon=String.valueOf(calculatedWon);
                    resultUsd=String.valueOf(usdTextView);
                }else if(cadTextView == null && wonTextView == null && usdTextView == null){
                        Toast.makeText(getApplicationContext(),"아무값이나 입력하세요!", Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            cadTextView.setText(resultCad);
            wonTextView.setText(resultWon);
            usdTextView.setText(resultUsd);
        }
    }
}//Main end