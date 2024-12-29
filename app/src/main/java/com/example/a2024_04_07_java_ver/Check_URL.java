package com.example.a2024_04_07_java_ver;


public class Check_URL {
    static boolean Check_LongURL = false; // URL 길이 확인
    static boolean Check_At = false; // '@' 삽입 확인
    static boolean Check_Double_Slash = false; // '//' 삽입 확인
    static boolean Check_Dash = false; // '-' 삽입 확인
    static boolean Check_Dot = false; // 서브도메인 확인


    public static void check_url(String URL){ // URL 내 이상 확인

        String Copy_URL = URL;

        String[] Check = {"https://www.","http://www.","https://","http://"}; //양식에 맞게 URL 변환
        for(int i =0; i<Check.length; i++) {
            if (Copy_URL.equals(Check[i]))
                Copy_URL = Copy_URL.substring(Check[i].length());
            break;
        }

        if(Copy_URL.equals("//")) // '//' 삽입 확인
            Check_Double_Slash = true;
        else
            Check_Double_Slash = false;

        int index = Copy_URL.indexOf("/");
        if (index != -1) {
            Copy_URL = Copy_URL.substring(0, index);
        }

        // '@' 삽입 확인
        if(Copy_URL.equals("@"))
            Check_At = true;
        else
            Check_At = false;

        // '-' 삽입 확인
        if(Copy_URL.equals("-"))
            Check_Dash = true;
        else
            Check_Dash = false;

        // URL 길이 확인
        if(Copy_URL.length() > 75)
            Check_LongURL = true;
        else
            Check_LongURL = false;

        // 서브도메인 개수 확인 ( 애매함 )
        if(Copy_URL.split(".").length > 3)
            Check_Dot = true;
        else
            Check_Dot = false;

    }



    public static String Check_URL_Answer(){
        int Check = 0;

        if(Check_Dot == true)
            Check++;

        if(Check_At == true)
            Check++;

        if(Check_Dash == true)
            Check++;

        if(Check_Double_Slash == true)
            Check++;

        if(Check_LongURL == true)
            Check++;


        if(Check==0){
            return "안전";
        }
        else if(Check<3) {
            Popup_Activity.answer_cnt++;
            return "주의";
        }
        else if(Check<5) {
            Popup_Activity.answer_cnt += 2;
            return "경고";
        }
        else {
            Popup_Activity.answer_cnt += 3;
            return "위험";
        }
    }

}
