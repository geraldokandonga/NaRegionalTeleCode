package com.holoog.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    TextView textIntro, textDefaultRegion, textPreference, textCustomMaster, textsetRegion, textGetRegion, textFullNumber;
    Button startDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        assignViews();
    }

    private void assignViews() {
        textIntro = (TextView) findViewById(R.id.textIntro);
        setclick(textIntro, 0);

        textDefaultRegion = (TextView) findViewById(R.id.textDefaultRegion);
        setclick(textDefaultRegion, 1);

        textPreference = (TextView) findViewById(R.id.textRegionPreference);
        setclick(textPreference, 2);

        textCustomMaster = (TextView) findViewById(R.id.textCustomMaster);
        setclick(textCustomMaster, 3);

        textsetRegion = (TextView) findViewById(R.id.textSetCountry);
        setclick(textsetRegion, 4);

        textGetRegion = (TextView) findViewById(R.id.textGetRegion);
        setclick(textGetRegion, 5);

        textFullNumber = (TextView) findViewById(R.id.textFullNumber);
        setclick(textFullNumber, 6);

        startDemo = (Button) findViewById(R.id.buttonGo);
        startDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                i.putExtra(MainActivity.EXTRA_INIT_TAB, 0);
                startActivity(i);
            }
        });
    }

    private void setclick(TextView text, final int tabIndex) {
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                i.putExtra(MainActivity.EXTRA_INIT_TAB, tabIndex);
                startActivity(i);
            }
        });
    }
}
