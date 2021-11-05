package com.example.myclockview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private MyClockView myClockView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myClockView = findViewById(R.id.my_clock_view);
        initData();
    }

    private void initData() {
        ArrayList list = new ArrayList<OutCircleData>();

        OutCircleData item = new OutCircleData();
        item.setStartAngle(-90);
        item.setEndAngle(0);
        item.setName("Red");
        list.add(item);

        item = new OutCircleData();
        item.setStartAngle(0);
        item.setEndAngle(80);
        item.setName("Green");
        list.add(item);

        item = new OutCircleData();
        item.setStartAngle(80);
        item.setEndAngle(150);
        item.setName("BLUE");
        list.add(item);

        item = new OutCircleData();
        item.setStartAngle(150);
        item.setEndAngle(270);
        item.setName("YELLOW");
        list.add(item);

        myClockView.setData(list);
    }

}