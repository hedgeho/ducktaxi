package com.example.ducktaxi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

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

        final boolean[] shouldSend = {false};

        findViewById(R.id.Send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shouldSend[0] = true;
            }
        });



        Thread Server = new Thread(new Runnable() {
            @Override
            public void run() {
                String fromclient = null;

                ServerSocket Server = null;
                try {
                    Server = new ServerSocket(8888);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println(e);
                }

                System.out.println ("TCPServer Waiting for client on port 8888");
                System.out.println(Utils.getIPAddress(true));

                while(true)
                {
                    Socket connected = null;
                    try {
                        assert Server != null;
                        connected = Server.accept();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println(e);
                    }
                    assert connected != null;
                    System.out.println( " THE CLIENT"+" "+ connected.getInetAddress() +":"+connected.getPort()+" IS CONNECTED ");

                    BufferedReader inFromClient = null;
                    BufferedWriter socketWriter = null;
                    try {
                        socketWriter = new BufferedWriter(new OutputStreamWriter(connected.getOutputStream(), "UTF-8"));
                        inFromClient = new BufferedReader(new InputStreamReader(connected.getInputStream(),"UTF-8"));
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println(e);
                    }

                    if (shouldSend[0]) {
                        try {
                            assert socketWriter != null;
                            socketWriter.write((X.getText()).toString() + " " + (Y.getText()).toString());
                            socketWriter.write("\n");
                            socketWriter.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        shouldSend[0] = false;
                    }


                    while ( true )
                    {
                        try {
                            assert inFromClient != null;
                            fromclient = inFromClient.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println(e);
                        }

                        assert fromclient != null;
                        if ( fromclient.equals("q") || fromclient.equals("Q") )
                        {
                            try {
                                connected.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.out.println(e);
                            }
                            break;
                        }
                        else
                        {
                            System.out.println( "RECIEVED:" + fromclient );
                        }
                    }
                }
            }
        });
        Server.start();
    }
}
