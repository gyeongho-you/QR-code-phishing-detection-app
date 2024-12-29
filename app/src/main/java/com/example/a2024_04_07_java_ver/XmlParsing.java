package com.example.a2024_04_07_java_ver;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class XmlParsing {

    static int Check_G_Name_Server = 0; // 메인서버 확인
    static int Check_G_Lifespan = 0; // 수명 확인
    static int Check_G_Creation_Date = 0; // 생성일 확인

    static int Check_K_Name_Server = 0; // 메인서버 확인
    static int Check_K_Lifespan = 0; // 수명 확인
    static int Check_K_Creation_Date = 0; // 생성일 확인

    public static int G_XmlParser(String Xml){

        Check_G_Name_Server = 0; // 메인서버 확인
        Check_G_Lifespan = 0; // 수명 확인
        Check_G_Creation_Date = 0; // 생성일 확인
        // 0 - 초기화 / 1 - 이상 / 2 - 정상

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(Xml));

            LocalDate NowTime = LocalDate.now(); // 현재 시간
            LocalDate Sub_Time;

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        // 태그의 시작
                        String tagName = parser.getName();
                        // 필요한 작업 수행
                        // 예를 들어, 태그가 "title"이면 해당 태그 내용을 가져올 수 있음

                        if ("parseCode".equals(tagName)){
                            String Code_Check = parser.nextText();
                            if(!(Code_Check.equals("3515")||Code_Check.equals("251")))
                                return 0; // 등록이 안됨
                        }

                        if("Address".equals(tagName)&&Check_G_Name_Server == 0){ // DNS 확인
                            String text = null;
                            try{
                                text = parser.nextText();

                                if(text != null && !text.isEmpty()) {
                                    System.out.println("DNS 존재");
                                    Check_G_Name_Server = 2;
                                }
                                else {
                                    System.out.println("DNS 없음 피싱");
                                    Check_G_Name_Server = 1;
                                }
                            }catch (Exception e){
                                text = null;
                                System.out.println("DNS 호출 오류");
                                Check_G_Name_Server = 1;
                            }

                        }
                        else if("expiresDate".equals(tagName)&&Check_G_Lifespan == 0){ // 수명확인
                            Sub_Time = NowTime.plus(6, ChronoUnit.MONTHS); // 현재시간에 6개월 추가 후 비교
                            LocalDate expiresData = null;
                            try {
                                String Save_expiresDate = parser.nextText();
                                Save_expiresDate = Save_expiresDate.split("T")[0];
                                expiresData = LocalDate.parse(Save_expiresDate);
                                if( expiresData.isBefore(Sub_Time)) {
                                    System.out.println("수명 6개윌 이하");
                                    Check_G_Lifespan = 1;
                                }
                                else {
                                    System.out.println("수명 6개윌 이상");
                                    Check_G_Lifespan = 2;
                                }
                            }catch (Exception e){
                                System.out.println("수명 호출 오류");
                                Check_G_Lifespan = 1;
                            }


                        }
                        else if("createdDate".equals(tagName)&&Check_G_Creation_Date == 0){ //생성일 확인
                            Sub_Time = NowTime;
                            LocalDate createData = null;
                            try {
                                String Save_createdDate = parser.nextText();
                                Save_createdDate = Save_createdDate.split("T")[0];
                                createData = LocalDate.parse(Save_createdDate); // 생성일에 1년 추가 후 비교
                                createData = createData.plus(12, ChronoUnit.MONTHS);
                                if(Sub_Time.isBefore(createData)) {
                                    System.out.println("생성 1년 이하");
                                    Check_G_Creation_Date = 1;
                                }
                                else {
                                    System.out.println("생성 1년 이상");
                                    Check_G_Creation_Date = 2;
                                }
                            }catch (Exception e){
                                System.out.println("생성일 호출 오류");
                                Check_G_Creation_Date = 1;
                            }

                        }

                }
                eventType = parser.next();
                if ( eventType == XmlPullParser.END_DOCUMENT )
                    return 1;
            }
        } catch (Exception e) {
            System.out.println("해외 XML 파싱 오류");
        }
        return 2;
    }

    public static int K_XmlParser(String Xml){

        Check_K_Name_Server = 0; // 메인서버 확인
        Check_K_Lifespan = 0; // 수명 확인
        Check_K_Creation_Date = 0; // 생성일 확인
        // 0 - 초기화 / 1 - 이상 / 2 - 정상

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(Xml));

            LocalDate NowTime = LocalDate.now(); // 현재 시간
            LocalDate Sub_Time;

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        // 태그의 시작
                        String tagName = parser.getName();
                        // 필요한 작업 수행
                        // 예를 들어, 태그가 "title"이면 해당 태그 내용을 가져올 수 있음

                        if ("error".equals(tagName)) { // 미 등록 사이트
                            return 0;
                        }

                        if("ns1".equals(tagName)&&Check_K_Name_Server == 0){
                            String text = null;
                            try {
                                text = parser.nextText();
                                if (text != null && !text.isEmpty()) {
                                    System.out.println("DNS 존재");
                                    Check_K_Name_Server = 2;
                                } else {
                                    System.out.println("DNS 없음 피싱");
                                    Check_K_Name_Server = 1;
                                }
                            }catch (Exception e){
                                System.out.println("DNS 호출 오류");
                                Check_K_Name_Server = 1;
                            }
                        }
                        else if("endDate".equals(tagName)&&Check_K_Lifespan == 0){
                            Sub_Time = NowTime.plus(6, ChronoUnit.MONTHS);
                            LocalDate expiresData = null;
                            try {
                                String Save_endDate = parser.nextText(); // 데이터 포멧 맞추기
                                Save_endDate = Save_endDate.substring(0,Save_endDate.length() -1);
                                Save_endDate = Save_endDate.replace(". ","-");
                                expiresData = LocalDate.parse(Save_endDate);
                                if (expiresData.isBefore(Sub_Time)) {
                                    System.out.println("수명 6개윌 이하");
                                    Check_K_Lifespan = 1;
                                } else {
                                    System.out.println("수명 6개윌 이상");
                                    Check_K_Lifespan = 2;
                                }
                            }catch (Exception e){
                                System.out.println("수명 호출 오류");
                                Check_K_Lifespan = 1;
                            }

                        }
                        else if("regDate".equals(tagName)&&Check_K_Creation_Date == 0){
                            Sub_Time = NowTime;
                            LocalDate createData = null;
                            try {
                                String Save_regDate = parser.nextText(); // 데이터 포멧 맞추기
                                Save_regDate = Save_regDate.substring(0,Save_regDate.length() -1);
                                Save_regDate = Save_regDate.replace(". ","-");
                                createData = LocalDate.parse(Save_regDate);
                                createData = createData.plus(12, ChronoUnit.MONTHS);

                                if (Sub_Time.isBefore(createData)) {
                                    System.out.println("생성 1년 이하");
                                    Check_K_Creation_Date = 1;
                                } else {
                                    System.out.println("생성 1년 이상");
                                    Check_K_Creation_Date = 2;
                                }
                            }catch (Exception e){
                                System.out.println("생성일 호출 오류");
                                Check_K_Creation_Date = 1;
                            }
                        }

                }
                eventType = parser.next();
                if ( eventType == XmlPullParser.END_DOCUMENT )
                    return 1;
            }
        } catch (Exception e) {
            System.out.println("국내 XML 파싱 오류");
        }
        return 2;
    }

    public static String Check_G_Parsing_Answer(){
        int Check = 0;

        if(Check_G_Creation_Date == 1)
            Check++;

        if(Check_G_Lifespan == 1)
            Check++;

        if(Check_G_Name_Server == 1)
            Check++;

        if(Check_G_Creation_Date == 0&&Check_G_Lifespan == 0&&Check_G_Name_Server == 0)
            Check = -1;

        if(Check==-1) {
            Popup_Activity.answer_cnt++;
            return "-";
        }
        else if(Check==0) {
            return "안전";
        }
        else if(Check<3){
            Popup_Activity.answer_cnt++;
            return "주의";
        }
        else {
            Popup_Activity.answer_cnt += 2;
            return "위험";
        }
    }

    public static String Check_K_Parsing_Answer(){
        int Check = 0;

        if(Check_K_Creation_Date == 1)
            Check++;

        if(Check_K_Lifespan == 1)
            Check++;

        if(Check_K_Name_Server == 1)
            Check++;

        if(Check_K_Creation_Date == 0&&Check_K_Lifespan == 0&&Check_K_Name_Server == 0)
            Check = -1;


        if(Check==-1) {
            Popup_Activity.answer_cnt++;
            return "-";
        }
        else if(Check==0) {
            return "안전";
        }
        else if(Check<3){
            Popup_Activity.answer_cnt++;
            return "주의";
        }
        else {
            Popup_Activity.answer_cnt += 2;
            return "위험";
        }
    }
}
