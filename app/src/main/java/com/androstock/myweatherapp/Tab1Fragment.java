package com.androstock.myweatherapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class Tab1Fragment extends Fragment {

    private static final String TAG = "Tab1Fragment";

    Typeface weatherFont;
    TextView refresh, cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, weatherIcon, updatedField, dlugoscGeograficzna, szerokoscGeograficzna;
    ProgressBar loader;

    String city = "LODZ, PL";
    String temperatura;
    /* Please Put your API KEY here */
    String OPEN_WEATHER_MAP_API = "57f41c13b644fa01648f4de748af6d41";
    /* Please Put your API KEY here */

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment,container,false);

        loader = view.findViewById(R.id.loader);
        refresh = view.findViewById(R.id.odswiez);
        cityField = view.findViewById(R.id.city_field);
        updatedField = view.findViewById(R.id.updated_field);
        detailsField = view.findViewById(R.id.details_field);
        currentTemperatureField = view.findViewById(R.id.current_temperature_field);
        weatherIcon = view.findViewById(R.id.weather_icon);
        weatherFont = ResourcesCompat.getFont(getContext(),R.font.weathericons_regular_webfont);
        weatherIcon.setTypeface(weatherFont);
        dlugoscGeograficzna = view.findViewById(R.id.dlugoscGeo);
        szerokoscGeograficzna = view.findViewById(R.id.szerokoscGeo);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        temperatura = sharedPreferences.getString("temperature", "C");
        city = sharedPreferences.getString("lokalizacja", "LODZ, PL");
        taskLoadUp(city);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {           //PRZYCISK ODSWIEZANIA
                taskLoadUp(city);
                Toast.makeText(getContext(), "Odświeżono :)", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        temperatura = sharedPreferences.getString("temperature", "C");
        city = sharedPreferences.getString("lokalizacja", "LODZ, PL");
        taskLoadUp(city);
        //taskLoadUp(temperatura);
        super.onResume();
    }

    class DownloadWeather extends AsyncTask< String, Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader.setVisibility(View.VISIBLE);
        }
        protected String doInBackground(String...args) {
            String xml = Function.excuteGet("http://api.openweathermap.org/data/2.5/weather?q=" + args[0] +
                    "&units=metric&appid=" + OPEN_WEATHER_MAP_API);
            return xml;
        }
        @Override
        protected void onPostExecute(String xml) {
            try {
                JSONObject json = new JSONObject(xml);
                if (json != null) {


                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    JSONObject coord = json.getJSONObject("coord");
                    DateFormat df = DateFormat.getDateTimeInstance();

                    DatabaseFragment1 dbHelper = new DatabaseFragment1(getContext());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("cityField", json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country"));
                    values.put("details", details.getString("description").toUpperCase(Locale.US));
                    values.put("dlugoscGeo", "Długość: " + coord.getString("lon"));
                    values.put("szerokoscGeo", "Szerokość: " + coord.getString("lat"));
                    values.put("ikona", (Function.setWeatherIcon(details.getInt("id"), json.getJSONObject("sys").getLong("sunrise") * 1000, json.getJSONObject("sys").getLong("sunset") * 1000)));
                    values.put("temperature", String.format("%.2f", main.getDouble("temp")));
                    values.put("updated", df.format(new Date(json.getLong("dt") * 1000)));


                    long newRowId = db.insert("tableFragment1", null, values);
                    if (newRowId == -1) {
                    } else {
                        Toast.makeText(getActivity(), "Zapisano do bazy!", Toast.LENGTH_SHORT).show();
                    }
                    cityField.setText(json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country"));
                    detailsField.setText(details.getString("description").toUpperCase(Locale.US));
                    dlugoscGeograficzna.setText("Długość: " + coord.getString("lon"));
                    szerokoscGeograficzna.setText("Szerokość: " + coord.getString("lat"));
                    weatherIcon.setText(Html.fromHtml(Function.setWeatherIcon(details.getInt("id"), json.getJSONObject("sys").getLong("sunrise") * 1000, json.getJSONObject("sys").getLong("sunset") * 1000)));

                    if (temperatura.equals("C")) {
                        currentTemperatureField.setText(String.format("%.2f", main.getDouble("temp")) + "°C");
                    } else {
                        currentTemperatureField.setText(String.format("%.2f", main.getDouble("temp") * 1.8 + 32) + "°F");
                    }
                    updatedField.setText(df.format(new Date(json.getLong("dt") * 1000)));
                    loader.setVisibility(View.GONE);
                }
                else{}

            } catch (JSONException e) {
                Toast.makeText(getContext(), "Błąd, podane miasto nie istnieje", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void taskLoadUp(String query) {
        if (Function.isNetworkAvailable(getContext())) {
            DownloadWeather task = new DownloadWeather();
            task.execute(query);
        } else {
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            DatabaseFragment1 dbHelper = new DatabaseFragment1(getContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String querys = "SELECT * FROM tableFragment1";
            Cursor cursor = db.rawQuery(querys, null);

            if (cursor.moveToLast()) {
                cityField.setText(cursor.getString(1)); //każdy numerek to kolejne pole
                detailsField.setText(cursor.getString(2));
                dlugoscGeograficzna.setText(cursor.getString(3));
                szerokoscGeograficzna.setText(cursor.getString(4));
                weatherIcon.setText(Html.fromHtml(cursor.getString(5)));
                currentTemperatureField.setText(cursor.getString(6));
                updatedField.setText(cursor.getString(7));
            }
        }
    }
}