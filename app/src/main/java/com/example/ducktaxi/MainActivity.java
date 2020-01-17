package com.example.ducktaxi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView X = (TextView) findViewById(R.id.X);
        final TextView Y = (TextView) findViewById(R.id.Y);

        ImageView MyMap = (ImageView) findViewById(R.id.Map);
        final int[] posXY = new int[2];
        MyMap.getLocationOnScreen(posXY);
        MyMap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float touchX = (int) event.getX();
                float touchY = (int) event.getY();

                float imageX = touchX - posXY[0];
                float imageY = touchY - posXY[1];

                float c_X = imageX/v.getWidth()*15;
                float c_Y = 15 - (imageY/v.getHeight()*15);

                X.setText("X: " + new DecimalFormat("##.##").format(c_X));
                Y.setText("Y: " + new DecimalFormat("##.##").format(c_Y));

                return true;
            }
        });
    }
}
