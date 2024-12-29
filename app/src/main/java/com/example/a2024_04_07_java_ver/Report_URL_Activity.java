package com.example.a2024_04_07_java_ver;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;

public class Report_URL_Activity extends AppCompatActivity {

    private Context RContext;
    private TextView report_URL1;
    private TextView report_URL2;
    private TextView report_URL3;
    private TextView report_URL4;
    private TextView report_URL5;
    private Button report_Btn1;
    private Button report_Btn2;
    private Button report_Btn3;
    private Button report_Btn4;
    private Button report_Btn5;
    private Button Back_Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        RContext = this;

        report_URL1 = findViewById(R.id.report_URL1);
        report_URL2 = findViewById(R.id.report_URL2);
        report_URL3 = findViewById(R.id.report_URL3);
        report_URL4 = findViewById(R.id.report_URL4);
        report_URL5 = findViewById(R.id.report_URL5);

        report_Btn1 = findViewById(R.id.report_Btn1);
        report_Btn2 = findViewById(R.id.report_Btn2);
        report_Btn3 = findViewById(R.id.report_Btn3);
        report_Btn4 = findViewById(R.id.report_Btn4);
        report_Btn5 = findViewById(R.id.report_Btn5);
        Back_Btn = findViewById(R.id.Back_Btn);

        report_URL1.setText(PreferenceManager.getString(this, "1"));
        report_URL2.setText(PreferenceManager.getString(this, "2"));
        report_URL3.setText(PreferenceManager.getString(this, "3"));
        report_URL4.setText(PreferenceManager.getString(this, "4"));
        report_URL5.setText(PreferenceManager.getString(this, "5"));

        report_Btn1.setOnClickListener(v -> Send_report(PreferenceManager.getString(RContext, "1")));
        report_Btn2.setOnClickListener(v -> Send_report(PreferenceManager.getString(RContext, "2")));
        report_Btn3.setOnClickListener(v -> Send_report(PreferenceManager.getString(RContext, "3")));
        report_Btn4.setOnClickListener(v -> Send_report(PreferenceManager.getString(RContext, "4")));
        report_Btn5.setOnClickListener(v -> Send_report(PreferenceManager.getString(RContext, "5")));

        Back_Btn.setOnClickListener(v -> finish());
    }

    private class SubmitDataAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.d("SubmitDataTask", "doInBackground 실행됨");
            String serverUrl = params[0];
            String inputUrl = params[1];
            String result;

            try {
                result = postRequest(serverUrl, inputUrl);
            } catch (IOException e) {
                Log.e("SubmitDataTask", "Error: " + e.getMessage(), e);
                result = "Error: " + e.getMessage(); // 에러 메시지를 결과에 추가
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.startsWith("Error")) {
                Log.e("SubmitDataTask", "Error: " + result);
                Toast.makeText(RContext, "전송 중 오류가 발생했습니다: " + result, Toast.LENGTH_LONG).show();
            } else {
                Log.d("SubmitDataTask", "Success: " + result);
                Toast.makeText(RContext, "성공적으로 전송되었습니다.", Toast.LENGTH_LONG).show();
            }
        }

        private String postRequest(String serverUrl, String inputUrl) throws IOException {
            URL url = new URL(serverUrl); // URL 객체 생성
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(false); // 자동 리다이렉트 비활성화
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // 전송할 데이터 설정
            String postData = "url=" + URLEncoder.encode(inputUrl, "UTF-8");

            // 데이터 전송
            sendPostData(conn, postData);


            int status = conn.getResponseCode();
            if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM) {
//                Log.d("SubmitDataTask", "send Error"); //확인
                String newUrl = conn.getHeaderField("Location");
                conn.disconnect();
                url = new URL(newUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                sendPostData(conn, postData);
            }

            // 응답 읽기
            return readResponse(conn);
        }

        private void sendPostData(HttpURLConnection conn, String postData) throws IOException {
            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData.getBytes("UTF-8"));
                os.flush();

            }
        }

        private String readResponse(HttpURLConnection conn) throws IOException {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            return sb.toString();
        }
    }

    private void Send_report(String url) {
        String lastReportTime = PreferenceManager.getString(RContext, "Report_Time");
        if (lastReportTime.isEmpty() || LocalDate.now().isAfter(LocalDate.parse(lastReportTime))) {// 신고 기록이 없거나, 오늘 신고하지 않은 것 확인
            PreferenceManager.setString(RContext, "Report_Time", LocalDate.now().toString());

            if (!url.isEmpty()) {
                // 프로토콜 확인 및 추가
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url;
                }
                new SubmitDataAsyncTask().execute("https://www.mosquito-net.shop/android_log_inset_php.php", url);
            } else {
                Log.e("SubmitDataTask", "URL is empty");
                Toast.makeText(RContext, "URL이 비어 있습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("SendReport", "오늘 신고 횟수가 초과되었습니다.");
            Toast.makeText(RContext, "오늘 신고 횟수가 초과되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}

