package com.example.turismo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class AuthentificationMenuActivity extends AppCompatActivity {

    Button loginButton;
    TextView register;
    TextView forgotPassword;

    public void createLoginListener()
    {
         loginButton =  findViewById(R.id.loginButton);
         register = findViewById(R.id.signup);
         forgotPassword = findViewById(R.id.forgotPassword);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthentificationMenuActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }

    public void createRegisterListener()
    {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthentificationMenuActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public void createForgotPasswordListener()
    {
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthentificationMenuActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    public void createListeners()
    {
        createLoginListener();
        createRegisterListener();
        createForgotPasswordListener();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentification_menu);
        createListeners();
    }
}