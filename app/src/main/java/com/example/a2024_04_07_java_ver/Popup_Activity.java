package com.example.a2024_04_07_java_ver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class Popup_Activity extends Activity {

    public static int answer_cnt = 0;

    private Context mContext;

    public static int Report_Count; // 신고 건수 저장
    TextView Pop_Link;

    TextView Pop_Title_Answer;

    TextView Pop_answer1;
    TextView Pop_answer2;
    TextView Pop_answer3;

    TextView Pop_answer4;

    Button Pop_yes;

    Button Pop_no;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        mContext = this;

        Pop_Link = findViewById(R.id.Pop_Link); // 주소 지정
        Pop_Title_Answer = findViewById(R.id.Pop_Title_Answer);
        Pop_answer1 = findViewById(R.id.Pop_answer1);
        Pop_answer2 = findViewById(R.id.Pop_answer2);
        Pop_answer3 = findViewById(R.id.Pop_answer3);
        Pop_answer4 = findViewById(R.id.Pop_answer4);
        Pop_yes = findViewById(R.id.yesButton);
        Pop_no = findViewById(R.id.noButton);


        Pop_Link.setText("Link : " + ScanQR.getScan_Link());
        Pop_answer1.setText("해외 Whois : " + ScanQR.getAnswer1() + "   /  파싱 결과 : " + XmlParsing.Check_G_Parsing_Answer());
        Pop_answer2.setText("국내 Whois : " + ScanQR.getAnswer2() + "   /  파싱 결과 : " + XmlParsing.Check_K_Parsing_Answer());
        Pop_answer3.setText("Google Safe Browsing : " + ScanQR.Google_Safe_result);
        Pop_answer4.setText("자체 검증 결과 : " + Check_URL.Check_URL_Answer() + "    //   신고 건수 : " + Report_Count);

        if(answer_cnt < 3){
            Pop_Title_Answer.setTextColor(Color.parseColor("#A0D468"));
            Pop_Title_Answer.setText("안전");
        }else if(answer_cnt < 5){
            Pop_Title_Answer.setTextColor(Color.parseColor("#FF7F00"));
            Pop_Title_Answer.setText("주의");
        }else{
            Pop_Title_Answer.setTextColor(Color.parseColor("#D1180B"));
            Pop_Title_Answer.setText("위험");
        }

        answer_cnt = 0; // 초기화


        Pop_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 확인 버튼 클릭 시 이벤트
                String text = PreferenceManager.getString(mContext,"Count");
                switch (Integer.parseInt(text)){ // URL 정보 저장
                    case 0:
                        PreferenceManager.setString(mContext,"Count", "1");
                        PreferenceManager.setString(mContext, "1", ScanQR.getScan_Link());
                        break;

                    case 1:
                        PreferenceManager.setString(mContext,"Count", "2");
                        PreferenceManager.setString(mContext, "2", ScanQR.getScan_Link());
                        break;

                    case 2:
                        PreferenceManager.setString(mContext,"Count", "3");
                        PreferenceManager.setString(mContext, "3", ScanQR.getScan_Link());
                        break;

                    case 3:
                        PreferenceManager.setString(mContext,"Count", "4");
                        PreferenceManager.setString(mContext, "4", ScanQR.getScan_Link());
                        break;

                    case 4:
                        PreferenceManager.setString(mContext,"Count", "5");
                        PreferenceManager.setString(mContext, "5", ScanQR.getScan_Link());
                        break;

                    case 5:
                        PreferenceManager.setString(mContext,"Count", "1");
                        PreferenceManager.setString(mContext, "1", ScanQR.getScan_Link());
                        break;
                }


                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(ScanQR.getScan_Link()));
                startActivity(intent);

                finish();
            }
        });

        Pop_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 취소 버튼 클릭 시 이벤트
                setResult(RESULT_OK);
                finish();
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if( event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }




}