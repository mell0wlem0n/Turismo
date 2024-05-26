package com.example.turismo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class WeatherFragment extends DialogFragment {

    private static final String ARG_LAT = "latitude";
    private static final String ARG_LNG = "longitude";
    private static final String API_KEY = "b84aafb0b73f854e0148980a6275cee7\n";

    private RecyclerView recyclerView;
    private WeatherAdapter weatherAdapter;
    private List<WeatherData> weatherDataList = new ArrayList<>();

    public static WeatherFragment newInstance(double lat, double lng) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LAT, lat);
        args.putDouble(ARG_LNG, lng);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        recyclerView = view.findViewById(R.id.weatherRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        weatherAdapter = new WeatherAdapter(weatherDataList);
        recyclerView.setAdapter(weatherAdapter);

        if (getArguments() != null) {
            double lat = getArguments().getDouble(ARG_LAT);
            double lng = getArguments().getDouble(ARG_LNG);
            fetchWeatherData(lat, lng);
        }
        return view;
    }

    private void fetchWeatherData(double lat, double lng) {
        String url = "https://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lng + "&appid=" + API_KEY + "&units=metric";
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray list = jsonObject.getJSONArray("list");
                        for (int i = 0; i < list.length(); i += 8) { // Get weather data for every 24 hours (8*3 hours intervals)
                            JSONObject weatherObject = list.getJSONObject(i);
                            String date = weatherObject.getString("dt_txt");
                            double temp = weatherObject.getJSONObject("main").getDouble("temp");
                            String weatherDescription = weatherObject.getJSONArray("weather").getJSONObject(0).getString("description");
                            weatherDataList.add(new WeatherData(date, temp, weatherDescription));
                        }
                        weatherAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("WeatherFragment", "Error fetching weather data", error)
        );
        queue.add(stringRequest);
    }

    private static class WeatherData {
        String date;
        double temp;
        String description;

        WeatherData(String date, double temp, String description) {
            this.date = date;
            this.temp = temp;
            this.description = description;
        }
    }

    private static class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
        private final List<WeatherData> weatherDataList;

        WeatherAdapter(List<WeatherData> weatherDataList) {
            this.weatherDataList = weatherDataList;
        }

        @NonNull
        @Override
        public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather, parent, false);
            return new WeatherViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
            WeatherData weatherData = weatherDataList.get(position);
            holder.dateText.setText(weatherData.date);
            holder.tempText.setText(String.valueOf(weatherData.temp) + "°C");
            holder.descriptionText.setText(weatherData.description);
        }

        @Override
        public int getItemCount() {
            return weatherDataList.size();
        }

        static class WeatherViewHolder extends RecyclerView.ViewHolder {
            TextView dateText;
            TextView tempText;
            TextView descriptionText;

            WeatherViewHolder(@NonNull View itemView) {
                super(itemView);
                dateText = itemView.findViewById(R.id.dateText);
                tempText = itemView.findViewById(R.id.tempText);
                descriptionText = itemView.findViewById(R.id.descriptionText);
            }
        }
    }
}
