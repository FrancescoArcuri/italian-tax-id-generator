package com.example.fiscalcodegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Calculate(View view){ //questo metodo mi permette di aprire una nuova activity
        Intent intent=new Intent(this, CalculateActivity.class); //il costruttore utilizza il parametro context (this) e definisce la classe a cui deve passare l'intent
        startActivity(intent); //passo l'intent alla funzione                  //cioé l'activity che deve partire
    }
}