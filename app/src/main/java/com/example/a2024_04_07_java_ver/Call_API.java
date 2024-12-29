package com.example.a2024_04_07_java_ver;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafeBrowsingThreat;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class Call_API {

    public static String K_Whois(URL QR_URL) throws IOException {

        String S_QR_URL = QR_URL.toString();
        String[] sub_QR_URL;

        String[] Check = {"https://www.","http://www.","https://","http://"}; //양식에 맞게 URL 변환
        for(int i = 0;i<Check.length;i++){
            if(S_QR_URL.contains(Check[i])) {
                S_QR_URL = S_QR_URL.substring(Check[i].length());
                break;
            }
        }

        int index = S_QR_URL.indexOf("/");
        if (index != -1) {
            S_QR_URL = S_QR_URL.substring(0, index);
        }

        StringBuilder urlBuilder = new StringBuilder("https://apis.data.go.kr/B551505/whois/domain_name"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=4qXQzXZEhApXqois1l5wYfqVtKRrix%2BwWiBLzQS1W9sSudI%2FY7wv79LsoxiMl1sKFImk%2BSxetB4p0w1gypO60Q%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("query","UTF-8") + "=" + URLEncoder.encode( S_QR_URL, "UTF-8")); /*도메인 이름 (.kr, .한국 도메인만 가능)*/
        urlBuilder.append("&" + URLEncoder.encode("answer","UTF-8") + "=" + URLEncoder.encode("xml", "UTF-8")); /*응답형식(XML/JSON) 을 지정(없으면 XML으로 응답)*/
        URL url = new URL(urlBuilder.toString());


        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Korea Response code: " + conn.getResponseCode());

        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        return  sb.toString();
    }

    public static String G_Whois(URL QR_URL) throws IOException {

        String S_QR_URL = QR_URL.toString();

        int index = S_QR_URL.indexOf("://");
        if (index != -1) {
            S_QR_URL = S_QR_URL.substring(index + 3); // "://" 다음 문자부터 추출
        }

        // "/" 이후 문자열은 필요 없으므로 잘라냄
        index = S_QR_URL.indexOf("/");
        if (index != -1) {
            S_QR_URL = S_QR_URL.substring(0, index);
        }

        StringBuilder urlBuilder = new StringBuilder("https://www.whoisxmlapi.com/whoisserver/WhoisService"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("apiKey","UTF-8") + "=at_jiD1nHHYyDnHOYGPV9artgOsTKdM6"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("domainName","UTF-8") + "=" + URLEncoder.encode( S_QR_URL , "UTF-8")); /*도메인 이름 (해외 도메인)*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("G_Response code: " + conn.getResponseCode());

        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        return  sb.toString();
    }

    public static String Google_Safe_Browsing(Activity activity,URL QR_URL) throws IOException, ExecutionException, InterruptedException {

        Tasks.await(SafetyNet.getClient(activity).initSafeBrowsing());
        SafetyNet.getClient(activity).lookupUri(QR_URL.toString(),
                        "AIzaSyCwqUX3IRPnZDlKczHwVFjejNOuEoQ9U1o",
                        SafeBrowsingThreat.TYPE_POTENTIALLY_HARMFUL_APPLICATION,
                        SafeBrowsingThreat.TYPE_SOCIAL_ENGINEERING)
                .addOnSuccessListener(activity,
                        new OnSuccessListener<SafetyNetApi.SafeBrowsingResponse>() {
                            @Override
                            public void onSuccess(SafetyNetApi.SafeBrowsingResponse sbResponse) {
                                if (sbResponse.getDetectedThreats().isEmpty()) {
                                    ScanQR.Google_Safe_result  = "정상";
                                } else {
                                    Popup_Activity.answer_cnt++;
                                    ScanQR.Google_Safe_result  = "비정상";
                                }
                            }
                        })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            ScanQR.Google_Safe_result  = "예외";
                            Log.d(TAG, "Error: " + CommonStatusCodes
                                    .getStatusCodeString(apiException.getStatusCode()));
                        } else {
                            Log.d(TAG, "Error: " + e.getMessage());
                            ScanQR.Google_Safe_result  = "예외";
                        }
                    }
                });

        SafetyNet.getClient(activity).shutdownSafeBrowsing();
        return null;
    }



}