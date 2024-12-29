package com.example.a2024_04_07_java_ver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class Check_Expend_URL {

    public static String unshortenUrl(String urlString) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                    responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                    responseCode == HttpURLConnection.HTTP_SEE_OTHER ||
                    responseCode == HttpURLConnection.HTTP_USE_PROXY ||
                    responseCode == 308) { // 응답코드 확인
                String newUrl = connection.getHeaderField("Location");
                return unshortenUrl(newUrl); // 새 URL 확인 후 이동
            } else if (responseCode == HttpURLConnection.HTTP_OK) {
                // 메타 데이터 확인
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder content = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                reader.close();
                String responseBody = content.toString();

                String metaRefreshTag = "<meta http-equiv=\"refresh\"";
                int metaIndex = responseBody.indexOf(metaRefreshTag);
                if (metaIndex != -1) {
                    String contentAttr = "content=\"";
                    int contentIndex = responseBody.indexOf(contentAttr, metaIndex);
                    if (contentIndex != -1) {
                        int start = contentIndex + contentAttr.length();
                        int end = responseBody.indexOf("\"", start);
                        String metaRedirectUrl = responseBody.substring(start, end);
                        String[] parts = metaRedirectUrl.split(";");
                        if (parts.length > 0) {
                            String[] urlParts = parts[0].split("=");
                            if (urlParts.length > 1) {
                                return unshortenUrl(urlParts[1].trim());
                            }
                        }
                    }
                }
                return urlString; //문자열 반환
            } else {
                throw new IOException("Unexpected response code: " + responseCode);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
