package com.androstock.myweatherapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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


import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class Tab2Fragment extends Fragment {

    private static final String TAG = "Tab2Fragment";

    Typeface weatherFont;
    TextView refresh, cityField, detailsField, currentTemperatureField, humidity_field, sila_wiatru, weatherIcon, updatedField, kierunek_wiatru, pressure_field;
    //ProgressBar loader1;

    String city = "LODZ, PL";
    /* Please Put your API KEY here */
    String OPEN_WEATHER_MAP_API = "57f41c13b644fa01648f4de748af6d41";
    /* Please Put your API KEY here */

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment,container,false);


        refresh = view.findViewById(R.id.odswiez);
        cityField = view.findViewById(R.id.city_field);
        detailsField = view.findViewById(R.id.details_field);
        pressure_field = view.findViewById(R.id.pressure_field);

        humidity_field = view.findViewById(R.id.wilgotnosc);
        sila_wiatru = view.findViewById(R.id.sila_wiatru);
        kierunek_wiatru = view.findViewById(R.id.kierunek_wiatru);
        weatherFont = ResourcesCompat.getFont(getContext(),R.font.weathericons_regular_webfont);


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

    class DownloadWeather extends AsyncTask< String, Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //loader1.setVisibility(View.VISIBLE);

        }

        protected String doInBackground(String... args) {
            String xml = Function.excuteGet("http://api.openweathermap.org/data/2.5/weather?q=" + args[0] +
                    "&units=metric&appid=" + OPEN_WEATHER_MAP_API);
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            try {
                JSONObject json1 = new JSONObject(xml);
                if (json1 != null) {

                    JSONObject main = json1.getJSONObject("main");
                    JSONObject wind = json1.getJSONObject("wind");

                    DatabaseFragment2 dbHelper = new DatabaseFragment2(getContext());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    ContentValues values1 = new ContentValues();

                    values1.put("silaWiatru", ("Siła wiatru: " + wind.getString("speed") + " m/s"));
                    values1.put("kierunekWiatru", ("Kierunek wiatru: " + wind.getString("deg")));
                    values1.put("wilgotnosc", ("Wilgotność: " + main.getString("humidity") + "%"));
                    values1.put("cisnienie", ("Ciśnienie: " + main.getString("pressure") + " hPa"));


                    long newRowId1 = db.insert("tableFragment2", null, values1);
                    if (newRowId1 == -1) {
                    } else {
                        Toast.makeText(getActivity(), "Zapisano do bazy 22222!", Toast.LENGTH_SHORT).show();
                    }

                    sila_wiatru.setText("Siła wiatru: " + wind.getString("speed") + " m/s");
                    kierunek_wiatru.setText("Kierunek wiatru: " + wind.getString("deg") + " stopni?");
                    humidity_field.setText("Wilgotność: " + main.getString("humidity") + "%");
                    pressure_field.setText("Ciśnienie: " + main.getString("pressure") + " hPa");

                } else {
                }

            } catch (JSONException e) {
                Toast.makeText(getContext(), "Błąd, podane miasto nie istnieje", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void taskLoadUp(String query) {
        if (Function.isNetworkAvailable(getContext())) {
            Tab2Fragment.DownloadWeather task = new Tab2Fragment.DownloadWeather();
            task.execute(query);
        } else {
            Toast.makeText(getContext(), "Brak połączenia z internetem", Toast.LENGTH_LONG).show();
            DatabaseFragment2 dbHelper = new DatabaseFragment2(getContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String querys1 = "SELECT * FROM tableFragment2";
            Cursor cursor = db.rawQuery(querys1, null);

            if (cursor.moveToLast()) {
                sila_wiatru.setText(cursor.getString(1));
                kierunek_wiatru.setText(cursor.getString(2));
                humidity_field.setText(cursor.getString(3));
                pressure_field.setText(cursor.getString(4));
            }
        }
    }
}
