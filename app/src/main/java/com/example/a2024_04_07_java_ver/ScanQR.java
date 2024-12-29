package com.example.a2024_04_07_java_ver;

import static com.example.a2024_04_07_java_ver.Check_URL.check_url;
import static com.example.a2024_04_07_java_ver.Popup_Activity.Report_Count;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanQR extends AppCompatActivity {

    private ExecutorService executorService = Executors.newSingleThreadExecutor(); //새 쓰레드
    private ActivityResultLauncher<Intent> popupLauncher;
    private String G_Whois_data;// 글로벌 후이즈 데이터 저장
    private String K_Whois_data;
    private URL exURL;
    private static String answer1 = "초기값";
    private static String answer2 = "초기값";

    private static String Scan_Link = "존재하지 않는 주소";

    public static String Google_Safe_result;

    public static String getScan_Link() {
        return Scan_Link;
    }

    public static String getAnswer1() {
        return answer1;
    }

    public static String getAnswer2() {
        return answer2;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false);
        integrator.setPrompt("Place a code on the angle");     // 옆에 뜨는 문구를 바꿀 수 있다.
        integrator.initiateScan();
        // 360도 전 방향으로 스캔할 수 있도록 코드 수정

        Scan_Link = "존재하지 않는 주소"; // 초기화
        answer1 = "초기값";
        answer2 = "초기값";
        Google_Safe_result = "";


        popupLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null ) {
                        // 팝업 액티비티가 정상적으로 종료된 경우
                        finish(); // 현재 액티비티 종료
                    }
                    else // 엑티비티를 강제 종료 했을 경우
                        finish();
                }
        );




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                // todo
            } else {

                try {
                    exURL = new URL(result.getContents());
                } catch (MalformedURLException e) {
                    System.out.print("URL 변환오류 \n");
                }

                executorService.execute(new CountReportTask());

                MyAsyncTask task = new MyAsyncTask();
                task.execute();


            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public class MyAsyncTask extends AsyncTask<Void, Void, Void> {//네트워크 통신을 위해 AsyncTask 상속
        @Override
        protected Void doInBackground(Void... voids) {// API를 이용하여 검사
            try {

                exURL = new URL(Check_Expend_URL.unshortenUrl(String.valueOf(exURL))); // 단축 URL 정상 URL로 변경

                Scan_Link = String.valueOf(exURL);
                check_url(Scan_Link); // URL 내 이상 확인

                G_Whois_data = Call_API.G_Whois(exURL);//Call_API class 함수 호출
                K_Whois_data = Call_API.K_Whois(exURL);
                Call_API.Google_Safe_Browsing(ScanQR.this,exURL);


            }catch (IOException e){
                System.out.print("API 호출 오류 \n");
            } catch (ExecutionException e) {
                System.out.print("API 호출 오류 \n");
            } catch (InterruptedException e) {
                System.out.print("API 호출 오류 \n");
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            int G_answer = XmlParsing.G_XmlParser(G_Whois_data);
            int K_answer = XmlParsing.K_XmlParser(K_Whois_data);


            if( G_answer == 0 ) {
                Popup_Activity.answer_cnt++;
                answer1 = "등록안됨";
            }
            else if( G_answer == 1 ) {
                answer1 = "등록확인";
            }
            else {
                Popup_Activity.answer_cnt++;
                answer1 = "API오류";
            }

            if( K_answer == 0 ) {
                Popup_Activity.answer_cnt++;
                answer2 = "등록안됨";
            }
            else if( K_answer == 1 ) {
                answer2 = "등록확인";
            }
            else {
                Popup_Activity.answer_cnt++;
                answer2 = "API오류";
            }

            Call_Popup();
        }
    }

    public void Call_Popup(){
        Intent intent = new Intent(ScanQR.this, Popup_Activity.class);
        popupLauncher.launch(intent);
    }


    private class CountReportTask implements Runnable {
        String url = exURL.toString();

        @Override
        public void run() {
            try {
                // Jsoup을 사용하여 웹 페이지 긁어오기
                Document document = Jsoup.connect("https://www.mosquito-net.shop/android_log_inset_php.php").get();
                // 전체 HTML 텍스트 가져오기
                String htmlText = document.toString();

                // 특정 문자열의 수 세기
                int count = countOccurrences(htmlText, url);

                // 네트워크 작업이 완료된 후 결과를 처리
                runOnUiThread(() -> {
                    Log.d("WebPage Data", "Count of '" + url + "': " + count);

                    if(count == 0) // 변수에 값 저장
                        Report_Count = 0;
                    else
                        Report_Count = count-1;
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 특정 문자열의 수를 세는 메서드
        private int countOccurrences(String text, String searchString) {
            int count = 0;
            int index = 0;
            while ((index = text.indexOf(searchString, index)) != -1) {
                count++;
                index += searchString.length();
            }
            return count;
        }
    }

}