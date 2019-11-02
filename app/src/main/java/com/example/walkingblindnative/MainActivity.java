package com.example.walkingblindnative;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private boolean isStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = (Button) findViewById(R.id.button);
        button.setText(getResources().getString(R.string.start_button));//set the text on button
        button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(isStarted){
                    button.setText(getResources().getString(R.string.start_button));//set the text on button
                    button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                    stopDesribeEngine();
                }else{
                    button.setText(getResources().getString(R.string.stop_button));//set the text on button
                    button.setBackgroundColor(getResources().getColor(R.color.colorRed));

                    startDesribeEngine();
                }isStarted = !isStarted;
            }
        });
    }

    private void startDesribeEngine(){

    }

    private void stopDesribeEngine(){

    }
}
