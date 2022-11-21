package com.enhancell.remotesample;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.verveba.ts3.R;

import java.util.Locale;

public class StationaryResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stationary_results);

        TextView avgDlTextView = findViewById(R.id.avgDlTextView);
        TextView avgUlTextView = findViewById(R.id.avgUlTextView);
        TextView maxDlTextView = findViewById(R.id.maxDlTextView);
        TextView maxUlTextView = findViewById(R.id.maxUlTextView);
        TextView avgPingTextView = findViewById(R.id.avgPingTextView);
        TextView moTextView = findViewById(R.id.moTextView);
        TextView mtTextView = findViewById(R.id.mtTextView);

        var bundle = getIntent().getExtras();
        double avgDl = bundle.getDouble("avgDl");
        double avgUl = bundle.getDouble("avgUl");
        double maxDl = bundle.getDouble("maxDl");
        double maxUl = bundle.getDouble("maxUl");
        double avgPing = bundle.getDouble("avgPing");
        int moPass = bundle.getInt("moPass");
        int mtPass = bundle.getInt("mtPass");

        avgDlTextView.setText(printThroughput((long) avgDl));
        avgUlTextView.setText(printThroughput((long) avgUl));
        maxDlTextView.setText(printThroughput((long) maxDl));
        maxUlTextView.setText(printThroughput((long) maxUl));
        avgPingTextView.setText(print(avgPing, 1, "ms"));
        moTextView.setText(moPass == 1 ? "Passed" : moPass == 0 ? "Failed" : "");
        mtTextView.setText(mtPass == 1 ? "Passed" : mtPass == 0 ? "Failed" : "");
    }

    @NonNull
    private static String print(double value, int decimals, @NonNull String unit) {
        String format = "%." + decimals + "f";
        return String.format(Locale.US, format, value) + " " + unit;
    }

    @NonNull
    public static String printThroughput(long value) {
        if (value < 1000) {
            return value + " bps";
        }
        if (value <= 999999) {
            return (value / 1000) + " kbps";
        }
        if (value <= 999999999) {
            return (value / 1000000) + " Mbps";
        }
        return (value / 1000000000) + " Gbps";
    }
}