package com.androstock.myweatherapp;

import android.content.SharedPreferences;

import static java.lang.Float.parseFloat;

public class Utilities {

    public static void setPreferences(SharedPreferences sharedPreferences){

        String latitude = sharedPreferences.getString("szerokosc", "51.756030");
        if(parseFloat(latitude) < (-78.999)){
            latitude = "-78.999";
            sharedPreferences.edit().putString("szerokosc", "-78.999").commit();
        }else if ( parseFloat(latitude) > (78.999) ){
            latitude = "78.999";
            sharedPreferences.edit().putString("szerokosc", "78.999").commit();
        }else{

        }
        String longtitude = sharedPreferences.getString("dlugosc", "19.466973");
        if(parseFloat(longtitude) < (-180)){
            latitude = "-180";
            sharedPreferences.edit().putString("dlugosc", "-180").commit();
        }else if ( parseFloat(longtitude) > (180) ){
            latitude = "180";
            sharedPreferences.edit().putString("dlugosc", "180").commit();
        }else{

        }
    }


    public static String getLatitude(SharedPreferences sharedPreferences){
        return sharedPreferences.getString("szerokosc", "51.756030");
    }

    public static String getLongtitude (SharedPreferences sharedPreferences){
        return sharedPreferences.getString("dlugosc", "19.466973");
    }

}
