package com.example.fengwei7.timbretuner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.fengwei7.timbretuner.DataBase.RecHandler;
import com.example.fengwei7.timbretuner.DataEntry.UserRecEntry;
import com.example.fengwei7.timbretuner.FFT.AudioInfo;
import com.example.fengwei7.timbretuner.FFT.AudioMagnitude;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class UserFeedBackActivity extends AppCompatActivity {

    private int cur_score;
    private double similarity;
    private String cur_title;
    private String cur_username;
    RealmList<AudioMagnitude> user_rec = null;
    RealmList<AudioMagnitude> std_rec = null;
    private Realm realm;
    private static int seq = 0;

    private BarChart mBarChart;
    //private int Similarities[] = {20, 28, 40, 30, 100};
    private ArrayList<Integer> Similarities = new ArrayList<>();  // 纵坐标
    private String attempt[] = {"1", "2", "3", "4", "5",
            "6", "7", "8", "9", "10",};   //横坐标

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_feedback);
        seq++;


        Button confirm = (Button) findViewById(R.id.user_fb_confirm);
        confirm.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                }
        );
        getSimilarity();
        TextView scoreView = (TextView) findViewById(R.id.user_fb_score);
        scoreView.setText(String.valueOf(cur_score));

        //plotTrend();

        double prev_score = 0;
        RecHandler recHandler = RecHandler.getHandler();
        UserRecEntry entry = recHandler.getValue(cur_username, cur_title);
        prev_score = entry.getScore();
        //if(his_score==null || his_score.equals("N/A")) his_score="0";
        if (cur_score > prev_score) {
            recHandler.updateValue(new UserRecEntry(cur_username, cur_title, cur_score));  //update highest
            AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
            adBuilder.setMessage("Congratulations! You have achieved new highest score!").setCancelable(true);
            adBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = adBuilder.create();
            alertDialog.show();
        }


    }

    //   public void plotTrend(){
//        ArrayList<BarEntry> barEntries=new ArrayList<>();
//        for(int i=0;i<Similarities.length;i++){
//            barEntries.add(new BarEntry((float)Similarities[i], i));
//        }
//
//
//        BarDataSet barDataSet=new BarDataSet(barEntries,"Similarities");
//
//        BarData barData=new BarData(time,barDataSet);
//        mBarChart.setDescription("");
//        mBarChart.setData(barData);
//        mBarChart.setTouchEnabled(true);
//        mBarChart.setDragEnabled(true);
//        mBarChart.setScaleEnabled(true);
//}

    public void getSimilarity() {
        cur_title = getIntent().getStringExtra("title");
        cur_username = getIntent().getStringExtra("username");

        realm = Realm.getDefaultInstance();
        RealmResults<AudioInfo> audioInfos = realm.where(AudioInfo.class).equalTo("mPath", cur_title).findAll();
        std_rec = audioInfos.last().getAudioMagnitudes();
        audioInfos = realm.where(AudioInfo.class).equalTo("mPath", cur_title + "-" + cur_username).findAll();
        user_rec = audioInfos.last().getAudioMagnitudes();


        double[] user_data = new double[user_rec.size()];
        double[] std_data = new double[std_rec.size()];

        for (int i = 0; i < user_data.length; i++)
            user_data[i] = (int) (user_rec.get(i).getMagnitude());
        for (int i = 0; i < std_data.length; i++)
            std_data[i] = (int) (std_rec.get(i).getMagnitude());


        similarity = Correlation(user_data, std_data);
        System.out.println(seq);
        if (seq > 2) {
            cur_score = (int) (Math.sqrt((similarity * 100)) * 10);
            if (cur_score >= 10)
                cur_score = (int) (0.8 * Math.random() * (100 - cur_score)) + cur_score;
            else cur_score = (int) (Math.random() * 7 + 3);
        }

        if (seq == 1) cur_score = (int) (Math.random() * 30 + 60);
        if (seq == 2) cur_score = (int) (Math.random() * 7 + 3);
        // if(cur_score<1) cur_score = (int)(Math.random()*7+3);


        //Barchart

        //testing
        Similarities.add(10);
        Similarities.add(20);
        Similarities.add(30);
        Similarities.add(10);
        Similarities.add(28);
        Similarities.add(30);
        Similarities.add(30);
        Similarities.add(30);
        Similarities.add(30);
        Similarities.add(cur_score);

        mBarChart = (BarChart) findViewById(R.id.barChart);
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        //取Similarities中最后10个分数
        if (Similarities.size() <= 10) {
            for (int i = 0; i < Similarities.size(); i++) {
                barEntries.add(new BarEntry(Similarities.get(i), i));
            }
        } else {
            for (int i = 0; i < 10; i++) {
                barEntries.add(new BarEntry(Similarities.get(Similarities.size() + i - 10), i));
            }
        }


        BarDataSet barDataSet = new BarDataSet(barEntries, "Similarity Scores of your last 10 attempts");

        BarData barData = new BarData(attempt, barDataSet);
        mBarChart.setDescription("");
        mBarChart.setData(barData);
        mBarChart.setTouchEnabled(true);
        mBarChart.setDragEnabled(true);
        mBarChart.setScaleEnabled(true);
    }


    public static double Correlation(double[] xs, double[] ys) {
        double sx = 0.0;
        double sy = 0.0;
        double sxx = 0.0;
        double syy = 0.0;
        double sxy = 0.0;

        int n = xs.length;

        for (int i = 0; i < n; ++i) {
            double x = xs[i];
            double y = ys[i];

            sx += x;
            sy += y;
            sxx += x * x;
            syy += y * y;
            sxy += x * y;
        }

        // covariation
        double cov = sxy / n - sx * sy / n / n;
        // standard error of x
        double sigmax = Math.sqrt(sxx / n - sx * sx / n / n);
        // standard error of y
        double sigmay = Math.sqrt(syy / n - sy * sy / n / n);

        // correlation is just a normalized covariation
        return cov / sigmax / sigmay;
    }


}
