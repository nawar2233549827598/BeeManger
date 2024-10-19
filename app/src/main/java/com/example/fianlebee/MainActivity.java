package com.example.fianlebee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView budgetCardView;
    private CardView calendar;
    private CardView confirming;
    private CardView myProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        budgetCardView = findViewById(R.id.budgetCardView);
        calendar = findViewById(R.id.calendar);
        confirming = findViewById(R.id.confirming);
        myProfile = findViewById(R.id.myprofile);


        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,Profile.class);
                startActivity(i);
            }
        });


        confirming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,CreditCard.class);
                startActivity(i);
            }
        });



        budgetCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,BudgetActivity.class);
                startActivity(i);
            }
        });

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,CalendarActivity.class);
                startActivity(i);
            }
        });


    }

    @Override
    public void onClick(View view) {

    }
}
