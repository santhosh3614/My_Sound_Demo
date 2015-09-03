package com.megaminds.sounddemoapp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.megaminds.sounddemoapp.custom.typeface.OpenSans;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int FEATURE_NOT_AVAILBLE = 12;
    private static final int POLL_INTERVAL = 100;

    @Bind(R.id.toolbar_top)
    Toolbar toolbarTop;
    @Bind(R.id.toolbacontent)
    TextView toolbacontent;
    @Bind(R.id.radiogroup)
    RadioGroup radiogroup;
    @Bind(R.id.quiet_radio_btn)
    RadioButton quietRadioBtn;
    @Bind(R.id.group_radio_btn)
    RadioButton groupRadioBtn;
    @Bind(R.id.noisy_radio_btn)
    RadioButton noisyRadioBtn;
    @Bind(R.id.quiet_dur_txtview)
    TextView quietDurTxtview;
    @Bind(R.id.group_dur_txtview)
    TextView groupDurTxtview;
    @Bind(R.id.noisy_dur_txtview)
    TextView noisyDurTxtview;

    private int startOffset;
    private double avgdB;
    private SoundMeter soundMeter;
    private boolean micPresent;

    private Handler handler = new Handler();

    private Runnable mRunPool = new Runnable() {
        @Override
        public void run() {
            updateTime(soundMeter.soundDb());
            handler.postDelayed(this, POLL_INTERVAL);
        }
    };
    private SoundMeterDb soundMeterDb;
    private SoundLevel soundlevel;


    private void updateTime(double db) {
        startOffset+=POLL_INTERVAL;
        avgdB+=db;
        Log.d(TAG, "updateTime....." + startOffset);
        if(startOffset==1000){
            db=avgdB/10;
            if (db <= 55) {
                quietRadioBtn.setChecked(true);
                soundlevel.quietLevel += startOffset;
                quietDurTxtview.setText(getDuration(soundlevel.quietLevel));
            } else if (db <= 70) {
                groupRadioBtn.setChecked(true);
                soundlevel.groupLevel += startOffset;
                groupDurTxtview.setText(getDuration(soundlevel.groupLevel));
            } else {
                noisyRadioBtn.setChecked(true);
                soundlevel.noiseLevel += startOffset;
                noisyDurTxtview.setText(getDuration(soundlevel.noiseLevel));
            }
            startOffset=0;
            avgdB=0;
        }
    }

    private String getDuration(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        return String.format("%d:%d:%d", hours, minutes, seconds);
    }

    private long getToadyMillis(){
        Calendar prefcal = Calendar.getInstance();
        prefcal.setTimeInMillis(System.currentTimeMillis());
        prefcal.set(Calendar.HOUR_OF_DAY, 0);
        prefcal.set(Calendar.MINUTE, 0);
        prefcal.set(Calendar.SECOND, 0);
        prefcal.set(Calendar.MILLISECOND, 0);

        return prefcal.getTimeInMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        soundMeterDb.updateSoundLevel(soundlevel);
        stopMeasuring();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startMeasuring();
        quietDurTxtview.setText(getDuration(soundlevel.quietLevel));
        groupDurTxtview.setText(getDuration(soundlevel.groupLevel));
        noisyDurTxtview.setText(getDuration(soundlevel.noiseLevel));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        soundMeterDb=new SoundMeterDb(this);
        long todayMillis=getToadyMillis();
        quietDurTxtview.setTypeface(OpenSans.getInstance(this).getTypeFace());
        groupDurTxtview.setTypeface(OpenSans.getInstance(this).getTypeFace());
        noisyDurTxtview.setTypeface(OpenSans.getInstance(this).getTypeFace());
        soundlevel = soundMeterDb.getSoundlevel(todayMillis);
        if(soundlevel==null){
            soundlevel=soundMeterDb.insertSoundLevel(todayMillis);
        }
        setSupportActionBar(toolbarTop);
        setTitle("");
        PackageManager pm = getPackageManager();
        micPresent = pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
        if (!micPresent) {
            showDialog(FEATURE_NOT_AVAILBLE);
            return;
        }
        soundMeter = new SoundMeter();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void startMeasuring() {
        if (micPresent) {
            soundMeter.start();
            handler.postDelayed(mRunPool, POLL_INTERVAL);
        }
    }

    private void stopMeasuring() {
        if (micPresent) {
            soundMeter.stop();
            handler.removeCallbacks(mRunPool);
            startOffset=0;
            avgdB=0;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == FEATURE_NOT_AVAILBLE) {
            return new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.no_availble_feture)
                    .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).create();
        } else return null;
    }

}
