package com.example.turismo;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.*;
import com.example.turismo.databinding.ActivityMapBinding;

public class MapActivity extends AppCompatActivity {

    ActivityMapBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new MapFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            //Log.e("CCCCCCC", "CCCCCCCC");
            if (item.getItemId() == R.id.mapItem) {
                replaceFragment(new MapFragment());
            } else if (item.getItemId() == R.id.accountItem) {
                //Log.e("BBBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBBB");
                replaceFragment(new AccountFragment());
            } else if (item.getItemId() == R.id.groupItem) {
                replaceFragment(new GroupFragment());
            } else if (item.getItemId() == R.id.calendarItem) {
                replaceFragment(new CalendarFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment f) {
        //Log.d("AAAAAAAAAAA", "IT CALLS");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame, f, null)
                .addToBackStack("fragmentTransaction")
                .commit();
    }
}