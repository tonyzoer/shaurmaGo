package com.example.zoer.shaurmago.services;

/**
 * Created by Zoer on 18.04.2017.
 */

public class StringHelper {
    public static String fromUtfToRus(String s){
        StringBuilder sb=new StringBuilder();
        for (Character c:s.toCharArray()
                ) {
            sb.append(c.toString());
        }
        return sb.toString();
    }
}
