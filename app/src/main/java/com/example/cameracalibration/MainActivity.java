package com.example.cameracalibration;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    Camera camera;
    FrameLayout frameLayout;
    ShowCamera showCamera;
    TextView textView;
    View rectView1;
    View rectView2;
    FloatingActionButton button;
    FloatingActionButton buttonToggle;
    int [] Margins1;
    int [] Margins2;
    boolean toggle=true;
    int Height;
    int Width;
    int userX;
    int userY;
    int margin = 20;
    FrameLayout.LayoutParams params;
    FrameLayout.LayoutParams params1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Margins1 = new int[4];
        Margins2 = new int[4];

        frameLayout = (FrameLayout)findViewById(R.id.frameLayout);
        frameLayout.setOnTouchListener(handleTouch);

        textView = (TextView) findViewById(R.id.textView);
        textView.setOnTouchListener(handleTouch);

        // open camera
        camera = Camera.open();
        showCamera = new ShowCamera(this, camera);
        frameLayout.addView(showCamera);

        Width = frameLayout.getWidth();
        Height = frameLayout.getHeight();

        Log.i("TAG", "W: " + Width + ", H: " + Height + ")");

        Margins1[0]=margin+500;
        Margins1[1]=margin+10;
        Margins1[2]=margin+500;
        Margins1[3]=margin+400;
        Margins2[0]=margin+500;
        Margins2[1]=margin+700;
        Margins2[2]=margin+500;
        Margins2[3]=margin+10;

        rectView1 = (View)findViewById(R.id.rectview1);

        rectView2 = (View)findViewById(R.id.rectview2);

        rectView1.setOnTouchListener(handleTouch);
        rectView2.setOnTouchListener(handleTouch);


        button = (FloatingActionButton) findViewById(R.id.captureBtn);
        buttonToggle = (FloatingActionButton) findViewById(R.id.captureBtn2);

        params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );

        params1 = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
//
//        params.setMargins(20, 30, 40, 50);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String toShow = "";
                if(toggle){

                    params.setMargins(Margins1[0],Margins1[1],Margins1[2],Margins1[3]);
                    rectView1.setLayoutParams(params);
                }
                else{

                    params1.setMargins(Margins2[0],Margins2[1],Margins2[2],Margins2[3]);
                    rectView2.setLayoutParams(params1);
                }
                toShow += String.valueOf(Margins1[0]) + " " +  String.valueOf(Margins1[1]) + " " +  String.valueOf(Width-Margins1[2]) + " " +  String.valueOf(Height-Margins1[3]);
                toShow += "\n" + String.valueOf(Margins2[0]) + " " +  String.valueOf(Margins2[1]) + " " +  String.valueOf(Width-Margins2[2]) + " " +  String.valueOf(Height-Margins2[3]);
                toShow += "\n" +"\n W:" + String.valueOf(Width) + " H:" + String.valueOf(Height);
                CharSequence cs = toShow;
                textView.setText(cs);
                textView.bringToFront();
//                Toast.makeText(getApplicationContext(),cs,Toast.LENGTH_LONG).show();
            }
        });

        buttonToggle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                toggle=!toggle;
                if(toggle){
                    rectView1.bringToFront();
                }
                else{
                    rectView2.bringToFront();
                }

            }
        });
    }


    public void onResume(Bundle savedInstanceState){
        Log.i("TAG", "in resume W: " + Width + ", H: " + Height + ")");
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //Here you can get the size!
        frameLayout = (FrameLayout)findViewById(R.id.frameLayout);
        Width = frameLayout.getWidth();
        Height = frameLayout.getHeight();

        Margins1[0]=((margin+500)*Width)/2005;
        Margins1[1]=((margin+10)*Height/1015);
        Margins1[2]=((margin+500)*Width)/2005;
        Margins1[3]=((margin+600)*Height/1015);
        Margins2[0]=((margin+500)*Width)/2005;
        Margins2[1]=((margin+600)*Height)/1015;
        Margins2[2]=((margin+500)*Width)/2005;
        Margins2[3]=((margin+10)*Height)/1015;

        params.setMargins(Margins1[0],Margins1[1],Margins1[2],Margins1[3]);
        rectView1.setLayoutParams(params);

        params1.setMargins(Margins2[0],Margins2[1],Margins2[2],Margins2[3]);
        rectView2.setLayoutParams(params1);

    }

    private View.OnTouchListener handleTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            userX = (int) event.getX();
            userY = (int) event.getY();

            Log.i("TAG", Boolean.toString(toggle));
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.i("TAG", "touched down");
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i("TAG", "moving: (" + userX + ", " + userY + ")");
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i("TAG", "touched up");
                    break;
            }
            if(toggle){
                //            find closest corner
                int x1 = Margins1[0], y1 = Margins1[1];
                int x2 = Width - Margins1[2], y2 = Height - Margins1[3];

                int dist_tl = calc_dist(x1,y1,userX,userY);
                int dist_tr = calc_dist(x2,y1,userX,userY);
                int dist_bl = calc_dist(x1,y2,userX,userY);
                int dist_br = calc_dist(x2,y2,userX,userY);

                Integer[] num = { dist_tl, dist_br, dist_tr, dist_bl};

                int min = Collections.min(Arrays.asList(num));
                String toShow = "";
                if(dist_tl==min){
                    Margins1[0]=userX;
                    Margins1[1]=userY;
                    rectView1.bringToFront();
                    toShow = String.valueOf(Margins1[0]) + " " +  String.valueOf(Margins1[1]) + " " +  String.valueOf(Margins1[2]) + " " +  String.valueOf(Margins1[3]);
                    params.setMargins(Margins1[0],Margins1[1],Margins1[2],Margins1[3]);
                    rectView1.setLayoutParams(params);
                }
                else if(dist_tr==min){
                    Margins1[2]=Width-userX;
                    Margins1[1]=userY;
                    rectView1.bringToFront();
                    toShow = String.valueOf(Margins1[0]) + " " +  String.valueOf(Margins1[1]) + " " +  String.valueOf(Margins1[2]) + " " +  String.valueOf(Margins1[3]);
                    params.setMargins(Margins1[0],Margins1[1],Margins1[2],Margins1[3]);
                    rectView1.setLayoutParams(params);
                }
                else if(dist_bl==min){
                    Margins1[0]=userX;
                    Margins1[3]=Height-userY;
                    rectView1.bringToFront();
                    toShow = String.valueOf(Margins1[0]) + " " +  String.valueOf(Margins1[1]) + " " +  String.valueOf(Margins1[2]) + " " +  String.valueOf(Margins1[3]);
                    params.setMargins(Margins1[0],Margins1[1],Margins1[2],Margins1[3]);
                    rectView1.setLayoutParams(params);
                }
                else if(dist_br==min){
                    Margins1[2]=Width-userX;
                    Margins1[3]=Height-userY;
                    rectView1.bringToFront();
                    toShow = String.valueOf(Margins1[0]) + " " +  String.valueOf(Margins1[1]) + " " +  String.valueOf(Margins1[2]) + " " +  String.valueOf(Margins1[3]);
                    params.setMargins(Margins1[0],Margins1[1],Margins1[2],Margins1[3]);
                    rectView1.setLayoutParams(params);
                }
            }
            else{
                //            move to new corner
                int X1 = Margins2[0], Y1 = Margins2[1];
                int X2 = Width - Margins2[2], Y2 = Height - Margins2[3];
                int Dist_tl = calc_dist(X1,Y1,userX,userY);
                int Dist_tr = calc_dist(X2,Y1,userX,userY);
                int Dist_bl = calc_dist(X1,Y2,userX,userY);
                int Dist_br = calc_dist(X2,Y2,userX,userY);

                Integer[] num = {Dist_bl,Dist_br,Dist_tl,Dist_tr };
                int min = Collections.min(Arrays.asList(num));
                String toShow = "";
                if(Dist_tl==min){
                    Margins2[0]=userX;
                    Margins2[1]=userY;
                    rectView2.bringToFront();
                    toShow = String.valueOf(Margins2[0]) + " " +  String.valueOf(Margins2[1]) + " " +  String.valueOf(Margins2[2]) + " " +  String.valueOf(Margins2[3]);
                    params1.setMargins(Margins2[0],Margins2[1],Margins2[2],Margins2[3]);
                    rectView2.setLayoutParams(params1);
                }
                else if(Dist_tr==min){
                    Margins2[2]=Width-userX;
                    Margins2[1]=userY;
                    rectView2.bringToFront();
                    toShow = String.valueOf(Margins2[0]) + " " +  String.valueOf(Margins2[1]) + " " +  String.valueOf(Margins2[2]) + " " +  String.valueOf(Margins2[3]);
                    params1.setMargins(Margins2[0],Margins2[1],Margins2[2],Margins2[3]);
                    rectView2.setLayoutParams(params1);
                }
                else if(Dist_bl==min){
                    Margins2[0]=userX;
                    Margins2[3]=Height-userY;
                    rectView2.bringToFront();
                    toShow = String.valueOf(Margins2[0]) + " " +  String.valueOf(Margins2[1]) + " " +  String.valueOf(Margins2[2]) + " " +  String.valueOf(Margins2[3]);
                    params1.setMargins(Margins2[0],Margins2[1],Margins2[2],Margins2[3]);
                    rectView2.setLayoutParams(params1);
                }
                else if(Dist_br==min){
                    Margins2[2]=Width-userX;
                    Margins2[3]=Height-userY;
                    rectView2.bringToFront();
                    toShow = String.valueOf(Margins2[0]) + " " +  String.valueOf(Margins2[1]) + " " +  String.valueOf(Margins2[2]) + " " +  String.valueOf(Margins2[3]);
                    params1.setMargins(Margins2[0],Margins2[1],Margins2[2],Margins2[3]);
                    rectView2.setLayoutParams(params1);
                }
            }

            return true;
        }
    };

    public int calc_dist(int x1, int y1, int x2, int y2){
        return (x1-x2)* (x1-x2) + (y1-y2) * (y1-y2);
    }

}