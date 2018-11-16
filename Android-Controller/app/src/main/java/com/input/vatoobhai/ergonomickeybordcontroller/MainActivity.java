package com.input.vatoobhai.ergonomickeybordcontroller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {


    MyObject myObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView text_main = findViewById(R.id.main_text_input_string);
        TextView text_status = findViewById(R.id.main_text_status);
        text_status.setText("Start");

        ImageView image_view = findViewById(R.id.home_state_diagram);

        myObject = new MyObject(this, text_main, text_status, image_view);

    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (!myObject.processGenericMotionEvent(event)){
            return super.onGenericMotionEvent(event);
        }
        return true;
    }

    private static final String Tag = "Cinput";

}
