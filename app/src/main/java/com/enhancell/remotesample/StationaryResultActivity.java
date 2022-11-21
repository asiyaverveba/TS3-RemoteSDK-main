package com.enhancell.remotesample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.verveba.ts3.R;
import androidx.appcompat.app.AppCompatActivity;


public class StationaryResultActivity extends AppCompatActivity {

    TextView text_max_dl,text_avg_dl;
    TextView text_max_ul,text_avg_ul;
    TextView text_ping_reponse;
    float max_dl,max_ul;
    float avg_dl,avg_ul;
    float min=60,max=70;
    float min1=30,max1=40;
    Bundle bundle;
    double pingTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stationary_result);

        //60 70 DL
        //30 40 UL

        text_max_dl=findViewById(R.id.max_dl_res);
       // text_max_ul=findViewById(R.id.max_ul_res);
        text_avg_dl=findViewById(R.id.avg_dl_res);
        //text_avg_ul=findViewById(R.id.avg_ul_res);
        text_ping_reponse=findViewById(R.id.pingresponsetime);

       // text_max_ul.setVisibility(View.INVISIBLE);
       // text_avg_ul.setVisibility(View.INVISIBLE);


        bundle = getIntent().getExtras();
        max_dl=bundle.getFloat("maxdl");
        avg_dl=bundle.getFloat("avgdl");
        pingTime=bundle.getDouble("pingTime");

//        max_ul=bundle.getFloat("maxul");
//        avg_ul=bundle.getFloat("avgul");
//
//        Log.v("StationaryResultActivity","max_dl :- "+max_dl);
//        Log.v("StationaryResultActivity","avg_dl :- "+avg_dl);
//        Log.v("StationaryResultActivity","max_ul :- "+max_ul);
//        Log.v("StationaryResultActivity","avg_ul :- "+avg_ul);


        text_max_dl.setText( max_dl+"");
        text_avg_dl.setText( avg_dl+"");
        text_ping_reponse.setText( pingTime+"");

       // text_max_ul.setText( max_ul+"");
       // text_avg_ul.setText( avg_ul+"");


    }

    public float getRandomNumber(float min, float max) {
        return (float) ((Math.random() * (max - min)) + min);
    }

}