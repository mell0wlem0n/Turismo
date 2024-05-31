package com.example.turismo;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class LoadingScreenActivity extends AppCompatActivity {
    private ImageView spinningGlobe;

    protected void startApplication() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadingScreenActivity.this, AuthentificationMenuActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2500);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        // Load the GIF into the ImageView
        ImageView gifProgressBar = findViewById(R.id.spinning_globe);
        Glide.with(this)
                .asGif()
                .load(R.drawable.where)
                .into(gifProgressBar);

        startApplication();
    }
}