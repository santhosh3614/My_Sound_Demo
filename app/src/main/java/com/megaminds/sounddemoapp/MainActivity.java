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

import com.megaminds.sounddemoapp.custom.SPTextView;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

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
    @Bind(R.id.current_decibels)
    SPTextView currentDecibels;
    private int FEATURE_NOT_AVAILBLE;
    private static final int POLL_INTERVAL = 100;
    private SoundMeter soundMeter;
    private boolean micPresent;

    private long quietSec, groupSec, noisySec;
    private Handler handler = new Handler();
    private Runnable mRunPool = new Runnable() {
        @Override
        public void run() {
            //TODO refresh UI
            updateTime(soundMeter.soundDb());
            handler.postDelayed(this, POLL_INTERVAL);
        }
    };

    private void updateTime(double db) {
        currentDecibels.setText(String.valueOf((int)db));
        if (db <= 55) {
            quietRadioBtn.setChecked(true);
            quietSec += POLL_INTERVAL;
            quietDurTxtview.setText(getDuration(quietSec));
        } else if (db <= 70) {
            groupRadioBtn.setChecked(true);
            groupSec += POLL_INTERVAL;
            groupDurTxtview.setText(getDuration(groupSec));
        } else {
            noisyRadioBtn.setChecked(true);
            noisySec += POLL_INTERVAL;
            noisyDurTxtview.setText(getDuration(noisySec));
        }
    }

//    String getTime(long sec) {
//        if (sec < 60) {
//            return "<1";
//        } else {
//            Log.d(TAG,"Sec:"+sec);
//            int mintes=(int)((float)sec / 60);
//            return String.valueOf(mintes);
//        }
//    }

    private void readApplicationPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        long millis = prefs.getLong("millis", System.currentTimeMillis());
        boolean isToday = isToday(millis);
        Log.d(TAG, "ISToday:" + isToday);
        if (!isToday) {
            prefs.edit().clear().commit();
        }
        quietSec = prefs.getLong("Quiet_millis", 0);
        groupSec = prefs.getLong("Group_millis", 0);
        noisySec = prefs.getLong("Noisy_millis", 0);
    }

    private String getDuration(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        return String.format("%d:%d:%d", hours, minutes, seconds);
    }

    private boolean isToday(long prefMillis) {
        Calendar prefcal = Calendar.getInstance();
        prefcal.setTimeInMillis(prefMillis);
        prefcal.set(Calendar.HOUR_OF_DAY, 0);
        prefcal.set(Calendar.MINUTE, 0);
        prefcal.set(Calendar.SECOND, 0);
        prefcal.set(Calendar.MILLISECOND, 0);

        prefMillis = prefcal.getTimeInMillis();
        long currentMillis = System.currentTimeMillis();
        Log.d(TAG, "Pref Time:" + new Date(prefMillis));
        Log.d(TAG, "Current Time:" + new Date(currentMillis));

        return currentMillis > prefMillis && currentMillis < (prefMillis + AlarmManager.INTERVAL_DAY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putLong("millis", System.currentTimeMillis()).putLong("Quiet_millis", quietSec)
                .putLong("Group_millis", groupSec)
                .putLong("Noisy_millis", noisySec).commit();
        stopMeasuring();
    }

    @Override
    protected void onResume() {
        super.onResume();
        readApplicationPreferences();
        startMeasuring();
        quietDurTxtview.setText(getDuration(quietSec));
        groupDurTxtview.setText(getDuration(groupSec));
        noisyDurTxtview.setText(getDuration(noisySec));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
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
