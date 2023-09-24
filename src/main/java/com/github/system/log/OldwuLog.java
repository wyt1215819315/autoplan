package com.github.system.log;

public class OldwuLog {

    private static String text = "";

    public static void clear(){
        text = "";
    }

    public static void log(String str){
        if (text.equals("")){
            text = text + str;
            return;
        }
        text = text + "\n" + str;
    }
    public static void error(String str){
        text = text + "\n[!!!ERROR!!!]" + str;
    }

    public static void warning(String str){
        text = text + "\n[!!WARN!!]" + str;
    }

    public static void logNoLine(String str){
        text = text + str;
    }

    public static String getLog(){
        return text;
    }

}
