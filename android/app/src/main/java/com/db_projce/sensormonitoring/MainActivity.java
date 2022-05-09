package com.db_projce.sensormonitoring;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView image_id1 = null;
    ImageButton image_id2 = null;
    TextView text_id1 = null;
    TextView text_id2 = null;
    TextView text_id3 = null;



    static  int temp = 0, hum =0, dust1 = 0, dust2 = 0, dust3 , index = 0; //db 온도값 , db 습도 , db 미세먼지 , index 선택인자
    static String status = null;
    //타이머 자식 쓰레드 생성 -- db 구문 업데이트 기능 쓰레드
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_id1 = findViewById(R.id.text_id1);//온도
        text_id2 = findViewById(R.id.text_id2);//습도
        text_id3 = findViewById(R.id.text_id3);//미세먼지 농도 2.5 기준
        image_id1 = findViewById(R.id.image_id1);//미세먼지 기준 배경 이미지
        image_id2 = findViewById(R.id.image_id2);//RGB button


        Handler text_changer = new Handler() {
            public void handleMessage(Message msg) {

                text_id1.setText(String.valueOf(temp)+" °C");
                text_id2.setText(String.valueOf(hum)+" %");
                text_id3.setText(String.valueOf(dust2)+" ㎍/㎥");

                //text_id1.setText(temp);
            }
        };



        //타이머 자식 쓰레드 생성 -- db 구문 업데이트 기능 쓰레드
        Timer timer = new Timer();
        timer = new Timer();

        TimerTask TT = new TimerTask() {
            @Override
            public void run() {
                // 반복실행할 구문

                //---- web requst line-----------//
                List<Request_db> data;

                String page = "http://cloud.park-cloud.co19.kr/project/view_android.php";

                try {
                    URL url = new URL(page);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    StringBuilder stringBuilder = new StringBuilder();

                    if(conn != null){

                        conn.setConnectTimeout(10000);
                        conn.setRequestMethod("GET");
                        conn.setUseCaches(false);
                        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){

                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                            while(true){
                                String line = bufferedReader.readLine();
                                if(line == null) break;
                                stringBuilder.append(line + "\n");
                            }
                            bufferedReader.close();
                        }
                        conn.disconnect();
                    }

                    System.out.println("서버에서 받아온 JSON 타입의 String : " + "\n" + String.valueOf(stringBuilder));

                    Gson gson = new Gson();

                    Type type = new TypeToken<List<Request_db>>() {}.getType();
                    System.out.println("type : "+type);
                    //data = gson.fromJson(String.valueOf(stringBuilder),type);
                    data = gson.fromJson(String.valueOf(stringBuilder), type);


                    //원하는 행의 데이터를 받아오는 곳 시간과 합쳐서 사용합니다.
                    String time = data.get(0).getTime();
//                    status = data.get(0).getAge();
                    temp = data.get(0).getTemp();
                    hum = data.get(0).getHum();
                    dust1 = data.get(0).getDust();
                    dust2 = data.get(0).getDust2();
                    dust3 = data.get(0).getDust3();
                    System.out.println("temp! : " + String.valueOf(temp));
                    System.out.println("hum! : " + String.valueOf(hum));
                    System.out.println("pm1.0! : " + String.valueOf(dust1));
                    System.out.println("pm2.5! : " + String.valueOf(dust2));
                    System.out.println("pm10! : " + String.valueOf(dust3));

                    text_changer.obtainMessage(1).sendToTarget(); //ui 변경 핸들러 호출

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        };//DB 업데이트 타이머 구문의 끝
        timer.schedule(TT, 0, 1000); //Timer 실행
    }
}