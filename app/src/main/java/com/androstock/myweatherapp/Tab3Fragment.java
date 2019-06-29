package com.androstock.myweatherapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Tab3Fragment extends Fragment {

    private static final String TAG = "Tab3Fragment";

    Typeface weatherFont;
    int test;
    TextView refresh, cityField, detailsField, currentTemperatureField, humidity_field, sila_wiatru, weatherIcon, kierunek_wiatru, pressure_field ,
            d1_date,d1_icon, d1_temperature, d1_details,
            d2_date,d2_icon, d2_temperature, d2_details,
            d3_date,d3_icon, d3_temperature, d3_details;
    //ProgressBar loader1;

    String city = "LODZ, PL";
    /* Please Put your API KEY here */
    String OPEN_WEATHER_MAP_API = "57f41c13b644fa01648f4de748af6d41";
    /* Please Put your API KEY here */

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_fragment, container, false);
        weatherFont = ResourcesCompat.getFont(getContext(), R.font.weathericons_regular_webfont);
        // loader1 = view.findViewById(R.id.loader);
        refresh = view.findViewById(R.id.odswiez);
        cityField = view.findViewById(R.id.city_field);

        detailsField = view.findViewById(R.id.details_field);
        pressure_field = view.findViewById(R.id.pressure_field);

        humidity_field = view.findViewById(R.id.wilgotnosc);
        sila_wiatru = view.findViewById(R.id.sila_wiatru);
        kierunek_wiatru = view.findViewById(R.id.kierunek_wiatru);

        d1_date = view.findViewById(R.id.d1_date);
        d1_icon = view.findViewById(R.id.d1_icon);
       // d1_details=view.findViewById(R.id.d1_details);
        d1_temperature=view.findViewById(R.id.d1_temperature);

        d2_date = view.findViewById(R.id.d2_date);
        d2_icon = view.findViewById(R.id.d2_icon);
       // d2_details=view.findViewById(R.id.d2_details);
        d2_temperature=view.findViewById(R.id.d2_temperature);

        d3_date = view.findViewById(R.id.d3_date);
        d3_icon = view.findViewById(R.id.d3_icon);
       // d3_details=view.findViewById(R.id.d3_details);
        d3_temperature=view.findViewById(R.id.d3_temperature);



        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        city = sharedPreferences.getString("lokalizacja", "LODZ, PL");
        taskLoadUp(city);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskLoadUp(city);
                Toast.makeText(getContext(), "Odświeżono :)", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        city = sharedPreferences.getString("lokalizacja", "LODZ, PL");
        taskLoadUp(city);
        super.onResume();
    }

    class DownloadWeather extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //loader1.setVisibility(View.VISIBLE);

        }

        protected String doInBackground(String... args) {
            String xml = Function.excuteGet("http://api.openweathermap.org/data/2.5/forecast?q=" + args[0] + "&units=metric&appid=" + OPEN_WEATHER_MAP_API);
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {


            try {
                JSONObject json = new JSONObject(xml);
                if (json != null) {

                    JSONArray list = json.getJSONArray("list");
                    JSONObject days;
                    String getDay, destDay;
                    Calendar calendar = new GregorianCalendar();

                    for(int i = 0; i < 40; i++) {
                        days = list.getJSONObject(i);
                        getDay = days.getString("dt_txt");
                        destDay = calendar.get(Calendar.YEAR) + "-0" + (calendar.get(Calendar.MONTH) + 1) + "-" + (calendar.get(Calendar.DAY_OF_MONTH) + 1) + " " + "15" + ":" + "00" + ":" + "00";
                        if(getDay.equals(destDay)) {
                            d1_icon.setText(Html.fromHtml(Function.setWeatherIcon(days.getJSONArray("weather").getJSONObject(0).getInt("id"), 1560219842 * 1000, 1560279571 * 1000)));
                            d1_date.setText(days.getString("dt_txt").substring(0,10));
                            d1_temperature.setText(String.format("%.2f",days.getJSONObject("main").getDouble("temp")) + "°C");
                        }
                        else if(days.getString("dt_txt").equals(calendar.get(Calendar.YEAR) + "-0" + (calendar.get(Calendar.MONTH) + 1) + "-" + (calendar.get(Calendar.DAY_OF_MONTH) + 2) + " " + "15" + ":" + "00" + ":" + "00")) {
                            d2_icon.setText(Html.fromHtml(Function.setWeatherIcon(days.getJSONArray("weather").getJSONObject(0).getInt("id"), 1560219842 * 1000, 1560279571 * 1000)));
                            d2_date.setText(days.getString("dt_txt").substring(0,10));
                            d2_temperature.setText(String.format("%.2f",days.getJSONObject("main").getDouble("temp")) + "°C");
                        }
                        else if(days.getString("dt_txt").equals(calendar.get(Calendar.YEAR) + "-0" + (calendar.get(Calendar.MONTH) + 1) + "-" + (calendar.get(Calendar.DAY_OF_MONTH) + 3) + " " + "15" + ":" + "00" + ":" + "00")) {
                            d3_icon.setText(Html.fromHtml(Function.setWeatherIcon(days.getJSONArray("weather").getJSONObject(0).getInt("id"), 1560219842 * 1000, 1560279571 * 1000)));
                            d3_temperature.setText(String.format("%.2f",days.getJSONObject("main").getDouble("temp")) + "°C");
                            d3_date.setText(days.getString("dt_txt").substring(0,10));
                        }
                        else{}
                    }
                }
            } catch (JSONException e) {
                Toast.makeText(getContext(), "Błąd, podane miasto nie istnieje", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void taskLoadUp(String query) {
        if (Function.isNetworkAvailable(getContext())) {
            Tab3Fragment.DownloadWeather task = new Tab3Fragment.DownloadWeather();
            task.execute(query);
        } else {
            Toast.makeText(getContext(), "Brak połączenia z internetem", Toast.LENGTH_LONG).show();
        }
    }
}