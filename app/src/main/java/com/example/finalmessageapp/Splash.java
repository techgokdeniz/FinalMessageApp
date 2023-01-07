package com.example.finalmessageapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash extends AppCompatActivity {

    Button LoginBtn,RegisterBtn;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    Thread wait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        LoginBtn = findViewById(R.id.btnlogin);
        RegisterBtn = findViewById(R.id.btnregister);
        firebaseAuth = FirebaseAuth.getInstance();
        SplashThread();

        user = firebaseAuth.getCurrentUser();

        if(user!=null){
            Toast.makeText(getApplicationContext(),"Giriş Yapmış Gözüküyorsunuz Yönlendiriyorum",Toast.LENGTH_SHORT).show();
            wait.start();
        }

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);

            }
        });

        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Register.class);
                startActivity(intent);

            }
        });

    }

    public void SplashThread(){
        wait = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }



}