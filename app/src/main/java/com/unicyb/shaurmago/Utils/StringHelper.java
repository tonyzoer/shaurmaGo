package com.unicyb.shaurmago.Utils;

public class StringHelper {
    public static String fromUtfToRus(String s) {
        StringBuilder sb = new StringBuilder();
        for (Character c : s.toCharArray()
                ) {
            sb.append(c.toString());
        }
        return sb.toString();
    }
}
