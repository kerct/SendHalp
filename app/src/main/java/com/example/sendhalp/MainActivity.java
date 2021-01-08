package com.example.sendhalp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.util.Range;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.amirarcane.lockscreen.activity.EnterPinActivity;
import com.example.sendhalp.listeners.ButtonListener;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PRESS_INTERVAL = 700;
    private static final int REQUEST_CODE = 123;

    public static final int BLINKER = 0;
    public static final int EMERGENCY = 1;
    public static final int LOCATION = 2;
    public static final int AUDIO = 3;
    public static final int POLICE = 4;

    public static final int BLINKER_INTERVAL = 0;
    public static final int MESSAGE_INTERVAL = 1;

    public static final int MAX_CHECKER_ARR_LEN = 5;
    public static final int MAX_SLIDER_ARR_LEN = 2;

    private Context currContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currContext = getApplicationContext();
        Intent intent = new Intent(currContext, EnterPinActivity.class);
        startActivity(intent);

        setContentView(R.layout.activity_main);

        CheckBox blinkerChecker = (CheckBox) findViewById(R.id.blinkerSelector);
        CheckBox emergencyChecker = (CheckBox) findViewById(R.id.emergencyMessage);
        CheckBox locaChecker = (CheckBox) findViewById(R.id.sendLoca);
        CheckBox audioChecker = (CheckBox) findViewById(R.id.recordAudio);
        CheckBox policeChecker = (CheckBox) findViewById(R.id.callPolice);

        RangeSlider blinkSlider = (RangeSlider) findViewById(R.id.blinkSlider);
        RangeSlider messageSlider = (RangeSlider) findViewById(R.id.messageSlider);

        Button fakeCallButton = (Button) findViewById(R.id.fakeCallBtn);

//        boolean isBlinkerChecked = ((CheckBox) findViewById(R.id.blinkerSelector)).isChecked();
//        boolean isEmergencyChecked = ((CheckBox) findViewById(R.id.emergencyMessage)).isChecked();
//        boolean isLocaChecked = ((CheckBox) findViewById(R.id.sendLoca)).isChecked();
//        boolean isAudioChecked = ((CheckBox) findViewById(R.id.recordAudio)).isChecked();
//        boolean isPoliceChecked = ((CheckBox) findViewById(R.id.callPolice)).isChecked();

        boolean allChecked[] = new boolean[MAX_CHECKER_ARR_LEN];
        int allSlider[] = new int[MAX_SLIDER_ARR_LEN];

        allSlider[BLINKER_INTERVAL] = 3;
        allSlider[MESSAGE_INTERVAL] = 2;

        for (int i = 0; i < MAX_CHECKER_ARR_LEN; i++) {
            allChecked[i] = false;
        }

        blinkerChecker.setChecked(allChecked[BLINKER]);
        emergencyChecker.setChecked(allChecked[EMERGENCY]);
        locaChecker.setChecked(allChecked[LOCATION]);
        audioChecker.setChecked(allChecked[AUDIO]);
        policeChecker.setChecked(allChecked[POLICE]);

        blinkSlider.setValues((float) allSlider[BLINKER_INTERVAL]);
        messageSlider.setValues((float) allSlider[MESSAGE_INTERVAL]);

        blinkerChecker.setOnClickListener(view -> allChecked[BLINKER] = blinkerChecker.isChecked());
        emergencyChecker.setOnClickListener(view -> allChecked[EMERGENCY] = emergencyChecker.isChecked());
        locaChecker.setOnClickListener(view -> allChecked[LOCATION] = locaChecker.isChecked());
        audioChecker.setOnClickListener(view -> allChecked[AUDIO] = audioChecker.isChecked());
        policeChecker.setOnClickListener(view -> allChecked[POLICE] = policeChecker.isChecked());

        blinkSlider.addOnChangeListener((slider, value, fromUser) -> allSlider[BLINKER_INTERVAL] = (int) value);
        messageSlider.addOnChangeListener((slider, value, fromUser) -> allSlider[MESSAGE_INTERVAL] = (int) value);

        fakeCallButton.setOnClickListener(view -> {
            Log.i(TAG, "Blinker = " + allChecked[BLINKER]);
            Log.i(TAG, "Emergency = " + allChecked[EMERGENCY]);
            Log.i(TAG, "Location = " + allChecked[LOCATION]);
            Log.i(TAG, "Audio = " + allChecked[AUDIO]);
            Log.i(TAG, "Police = " + allChecked[POLICE]);
            Log.i(TAG, "Blinker Interval = " + allSlider[BLINKER_INTERVAL]);
            Log.i(TAG, "Message Interval = " + allSlider[MESSAGE_INTERVAL]);
        });

//        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        ComponentName buttonListener = new ComponentName(this, ButtonListener.class);
//        audioManager.registerMediaButtonEventReceiver(buttonListener);

//        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
//        ButtonListener buttonListerService = new ButtonListener();
//        currContext.registerReceiver(buttonListerService, filter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long timeDiffBetweenPresses = Math.abs(event.getDownTime() - event.getEventTime());
        if (KeyEvent.KEYCODE_VOLUME_DOWN == event.getKeyCode() || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (timeDiffBetweenPresses < PRESS_INTERVAL && timeDiffBetweenPresses != 0) {
                Intent intent = new Intent(this, Termination.class);
                startActivity(intent);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}