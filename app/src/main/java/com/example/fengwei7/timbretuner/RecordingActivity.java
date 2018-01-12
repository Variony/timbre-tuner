package com.example.fengwei7.timbretuner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fengwei7.timbretuner.DataBase.DataHandler;
import com.example.fengwei7.timbretuner.DataEntry.DataEntry;
import com.example.fengwei7.timbretuner.FFT.AudioInfo;
import com.example.fengwei7.timbretuner.FFT.AudioMagnitude;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import ca.uol.aig.fftpack.RealDoubleFFT;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class RecordingActivity extends AppCompatActivity {

    //AudioRecord对象使用8khz频率，单音频通道，16位样本
    //采样率是指每秒采样多少次
    //采样位数是指每次采样的位数，常见的是8位或16位，也就是声卡的分辨率
    //单音频通道，所以每次采样同一个通道
    private int frequency=8000;
    private int channelConfiguration=AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioEncoding= AudioFormat.ENCODING_PCM_16BIT;
    private Context mContext=this;
    private static String BACKUP_DB_PATH_DIR = null;
    private String cur_title=null;
    private String cur_comment=null;
    private String cur_username=null;
    private String audioName =null;

    //Transformer 将是我们FFT对象，通过FFT对象，可以一次性处理来自AudioRecord象的256个样本。 使用的样本数量将对应于通过FFT
    //对象运行他们之后获得的分量频率数量，虽然可以自由选择不同的大小，但要考虑内存和性能的问题。
    private RealDoubleFFT transformer;
    private int blockSize=256;
    //自己定义的内部类
    private RecordAudio recordTask;
    //通过canvas和paint再imageView中绘制音频流各种频率的级别
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;
    private Paint standardPaint;
    //UI
    private ImageView imageView;
    private Button startStopButton;
    private AudioRecord audioRecord;
    private boolean started=false;
    private long start_time=0;
    private long end_time = 0;

    private Realm  realm;
    double[] toTransform = new double[blockSize];
    double[] final_rec;
    List<double[]> rec_list = new ArrayList<double []>();

    RealmList<AudioMagnitude> cur_rec=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rec_cmp);

        startStopButton= (Button) findViewById(R.id.StartStopButton);
        //standardButton= (Button) findViewById(R.id.standardButton);

        transformer=new RealDoubleFFT(blockSize);

        cur_title = getIntent().getStringExtra("title");
        cur_comment=getIntent().getStringExtra("comment");
        cur_username=getIntent().getStringExtra("username");
        imageView=(ImageView) findViewById(R.id.imageView_recording);
        //宽：256（blockSize）长：100
        bitmap=Bitmap.createBitmap((int)128, (int)100, Bitmap.Config.ARGB_8888);
        canvas=new Canvas(bitmap);

        paint=new Paint();
        paint.setColor(Color.GREEN);
        imageView.setImageBitmap(bitmap);

        canvas.drawColor(Color.BLACK);
        int a = 0;
        standardPaint = new Paint();
        standardPaint.setColor(Color.RED);

        realm = Realm.getDefaultInstance();
        RealmResults<AudioInfo> audioInfos = realm.where(AudioInfo.class).equalTo("mPath",cur_title).findAll();

        if(!audioInfos.isEmpty()) {
            // RealmList<AudioMagnitude> audioMagnitudes = audioInfos.last().getAudioMagnitudes();

            cur_rec=audioInfos.last().getAudioMagnitudes();

            for (AudioMagnitude audioMagnitude : cur_rec) {
                canvas.drawLine(a, 100 - (float)(audioMagnitude.getMagnitude() * 10.1), a, 100, standardPaint);
                a++;
            }
        }



        //RealDoubleFFT类constructor接受每次处理样品数量作为参数。也代表了输出不同频率的数量。

        //imageView的设置和用于绘图的相关对象

        BACKUP_DB_PATH_DIR = "/data/data/audio";



        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(started){
                    started=false;
                    startStopButton.setText("Start");
                    recordTask.cancel(true);
                    //    Toast.makeText(RecordingActivity.this, String.valueOf(rec_list.size()),
                    //            Toast.LENGTH_SHORT).show();

                    Toast.makeText(RecordingActivity.this, "Recording Finished",
                            Toast.LENGTH_SHORT).show();


                    canvas.drawColor(Color.BLACK);
                    int a = 0;
                    standardPaint = new Paint();
                    standardPaint.setColor(Color.RED);
                    paint = new Paint();
                    standardPaint.setColor(Color.GREEN);



                    double[] ave_rec = new double[blockSize];
                    for(double[] one_rec:rec_list){
                        for(int i=0;i<ave_rec.length;i++)
                            ave_rec[i]+=one_rec[i]*5;
                    }
                    for(int i=0;i<ave_rec.length;i++) {
                        ave_rec[i] /= rec_list.size();
                        if(ave_rec[i]>10) ave_rec[i]=10;

                    }

                    realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    AudioInfo audioInfo = realm.createObject(AudioInfo.class);
                    if(cur_username.equals("none"))audioInfo.setPath(cur_title);
                    else audioInfo.setPath(cur_title+"-"+cur_username);

                    for (AudioMagnitude audioMagnitude : getAudioMagnitudes(ave_rec) ){
                        audioInfo.getAudioMagnitudes().add(realm.copyToRealm(audioMagnitude));
                    }
                    realm.commitTransaction();



                    realm = Realm.getDefaultInstance();
                    RealmResults<AudioInfo> audioInfos = realm.where(AudioInfo.class).equalTo("mPath",cur_title).findAll();

                    if(!audioInfos.isEmpty()) {
                        // RealmList<AudioMagnitude> audioMagnitudes = audioInfos.last().getAudioMagnitudes();

                        cur_rec=audioInfos.last().getAudioMagnitudes();

                        for (AudioMagnitude audioMagnitude : cur_rec) {
                        //    System.out.println(audioMagnitude.getMagnitude());
                            canvas.drawLine(a, 100 - (float)(audioMagnitude.getMagnitude() * 10.1), a, 100, standardPaint);
                            a++;
                        }
                    }



                    if(cur_username.equals("none")) {
                        DataHandler handler = DataHandler.getHandler();
                        if (audioName != null) {
                            handler.updateValue(new DataEntry(cur_title, cur_comment, audioName));
                        }

                        new Handler().postDelayed(new Runnable(){
                            public void run(){

                                //启动主Activity后销毁自身
                                finish();
                            }
                        },2000);
                    }
                    else{
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                Intent i = new Intent(RecordingActivity.this, UserFeedBackActivity.class);
                                i.putExtra("title", cur_title);
                                i.putExtra("username", cur_username);
                                startActivity(i);
                                finish();
                            }
                        },2000);
                    }




                }
                else{
                    started=true;

                    start_time=System.currentTimeMillis();
                    end_time= start_time+4000;
                    startStopButton.setText("Stop");
                    recordTask=new RecordAudio();
                    recordTask.execute();

                }
            }
        });



    }


    private class RecordAudio extends AsyncTask<Void, double[], Void> {

        @Override
        protected Void doInBackground(Void... params) {
            DataOutputStream dos=null;
            BufferedOutputStream os=null;
            try {

                //建立和使用AudioRecord
                int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);

                audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
                        channelConfiguration, audioEncoding, bufferSize);

                //short类型的buffer接受来自AudioRecord对象原始PCM样本。Double类型的toTransform将以双精度形式储存相同数据，
                // 因为FFT类需要
                short[] buffer = new short[bufferSize];

                audioRecord.startRecording();


                    File createOutFile = new File(BACKUP_DB_PATH_DIR);
                    if (!createOutFile.exists()) {
                        createOutFile.mkdir();
                    }
                    if(cur_username.equals("none")) {
                        audioName = String.valueOf(cur_title);
                    }
                    else{
                        audioName = String.valueOf(cur_title+"-"+cur_username);
                    }
                    String path = getExternalFilesDir(BACKUP_DB_PATH_DIR).toString() + "/" + audioName;

                    os = new BufferedOutputStream(new FileOutputStream(path));
                    dos = new DataOutputStream(os);


                while (started ) {

                    int bufferReadResult=audioRecord.read(buffer,0,bufferSize);


                    for (int j = 0; j < bufferReadResult; j++) {
                        dos.writeShort(buffer[j]);
                    }


                    //从AudioRecord对象中读取数据之后进行循环，并将short转成double。但是不能直接cast转
                    //因为期望值再-1到1之间，而不是整个范围，将short除以MaxValue达成此目的，因为short类型的最大值。
                    for (int i = 0; i < 256; i++) {
                        toTransform[i] = (double) buffer[i] / Short.MAX_VALUE;//有符号16位
                    }
                    //将双精度数值给FFT。FFT对象重用这个数组来保存输出值。包含的数据采用frequency domain。
                    //由于使用256值且采样率为8000，因此可以确定每个数组每个元素将近覆盖15.625HZ。 经采样率除以2（因为捕获最高频率是采样的一半）
                    //然后除256，就得到此数字。因此数组中的第一个元素代表0-15.625HZ的音频级别。
                    transformer.ft(toTransform);
                    publishProgress(toTransform);

                }

                audioRecord.release();
                audioRecord.stop();
                os.close();
                dos.close();








            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }




        //在屏幕上将数据绘成一系列最大100像素高的线。每一条线表示数组中的一个元素，因此范围是15.625HZ。第一条线表示的频率再0-15.625HZ，
        //而最后一条在3984.375-4000Hz
        @Override
        protected void onProgressUpdate(double[]... toTransform) {
            super.onProgressUpdate(toTransform);
            if(System.currentTimeMillis()>end_time) startStopButton.performClick();
            else if(System.currentTimeMillis()>start_time+1000 && System.currentTimeMillis()<end_time-2000) {
                final_rec= new double[blockSize];
                for(int i=0;i<final_rec.length;i++)
                    final_rec[i]=toTransform[0][i];
                rec_list.add(final_rec);
         //       for(int i=0;i<final_rec.length;i++)
          //          if(final_rec[i]<0.2) final_rec[i]+=Math.random()*1;

                //  rec_list.add(toTransform[0]);

            }


            canvas.drawColor(Color.BLACK);
            int a = 0;
            standardPaint = new Paint();
            standardPaint.setColor(Color.RED);

            realm = Realm.getDefaultInstance();
            RealmResults<AudioInfo> audioInfos = realm.where(AudioInfo.class).equalTo("mPath",cur_title).findAll();

            if(!audioInfos.isEmpty()) {
                // RealmList<AudioMagnitude> audioMagnitudes = audioInfos.last().getAudioMagnitudes();

                if(cur_rec==null)
                    cur_rec=audioInfos.last().getAudioMagnitudes();

                for (AudioMagnitude audioMagnitude : cur_rec) {
                    canvas.drawLine(a, 100 - (float)(audioMagnitude.getMagnitude() * 10.1), a, 100, standardPaint);
                    a++;


              }

            }
            else{

            }


            if(System.currentTimeMillis()<end_time-50) {
                for (int i = 0; i < toTransform[0].length/2+1; i++) {
                    int x = i;
                    float downy = (float) (100 - (toTransform[0][i] * 10));
                    float upy = 100;
                    canvas.drawLine(x, downy, x, upy, paint);

                }

                imageView.invalidate();
            }
        }
    }



    private ArrayList<AudioMagnitude> getAudioMagnitudes(double[] doubleArray){
        ArrayList<AudioMagnitude> audioMagnitudes=new ArrayList<>();
        for(double d:doubleArray){
            AudioMagnitude audioMagnitude=new AudioMagnitude();
            audioMagnitude.setMagnitude((float) d);
            audioMagnitudes.add(audioMagnitude);
        }
        return audioMagnitudes;

    }



}