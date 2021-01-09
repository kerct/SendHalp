package com.example.sendhalp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//
//import java.util.Locale;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amirarcane.lockscreen.activity.EnterPinActivity;
import com.google.android.material.slider.RangeSlider;

import java.io.IOException;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

    private static final int LOCATION_REQUEST_CODE = 34;

    /**
     * Provides the entry point to the Fused Location Provider API.
     */
//    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

//    private TextView mLatitudeText;
//    private TextView mLongitudeText;

    private MediaRecorder recorder = null;
    private String fileName = null;

    Button flashLightBtn;
    private final int CAMERA_REQUEST_CODE = 2;
    private final int MIC_REQUEST_CODE = 0;
    boolean hasCameraFlash = false;
    private boolean isFlashOn = false;
    int k = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currContext = getApplicationContext();
        Intent intent = new Intent(currContext, EnterPinActivity.class);
        startActivity(intent);

        setContentView(R.layout.activity_main);
//        View view = getRootView().findViewById(_id);

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
      
        //for audio record
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecord.3gp";

        hasCameraFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

//        mLatitudeText = (TextView) findViewById((R.id.latitude_text));
//        mLongitudeText = (TextView) findViewById((R.id.longitude_text));
//
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

//        flashLightBtn = findViewById(R.id.Flash);
//
//        flashLightBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                askPermission(Manifest.permission.CAMERA, CAMERA_REQUEST_CODE);
//
//            }
//        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long timeDiffBetweenPresses = Math.abs(event.getDownTime() - event.getEventTime());
        if (KeyEvent.KEYCODE_VOLUME_DOWN == event.getKeyCode() || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (timeDiffBetweenPresses < PRESS_INTERVAL && timeDiffBetweenPresses != 0) {
//                Intent intent = new Intent(this, Termination.class);
//                startActivity(intent);
                askCameraPermission(Manifest.permission.CAMERA, CAMERA_REQUEST_CODE);
                flashScreen();
                playAlarm();
                askMicPermission(Manifest.permission.RECORD_AUDIO, MIC_REQUEST_CODE);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //start audio recording
    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
        }

        recorder.start();
    }

    //stop audio recording
    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    public void playAlarm() {
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alert);
        r.play();

        Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            int i = 0;
            @Override
            public void run() {
                audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
        //r.stop();
    }

    public void flashScreen() {
        androidx.constraintlayout.widget.ConstraintLayout bgElement
                = (androidx.constraintlayout.widget.ConstraintLayout) findViewById(R.id.container);

        Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            int i = 0;
            int j = 0;
            int delay = 1000;
            @Override
            public void run() {
                if (k == 3) {
                    bgElement.setBackgroundColor(Color.WHITE);
                } else {
                    if (i == 0) {
                        bgElement.setBackgroundColor(Color.BLACK);
                        i = i + 1;
                        j = j + 1;
                    } else {
                        bgElement.setBackgroundColor(Color.WHITE);
                        i = 0;
                    }
                    if (j % 5 == 0) {
                        if (delay > 500) {
                            delay = delay - 100;
                        }
                    }
                }

                handler.postDelayed(this, delay);
            }
        };
        // Start the Runnable immediately
        handler.post(runnable);
        if (k == 3) {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 5000);
        }

    }

    public void general(View view) {
        //playAlarm(view);  play alarm sound
        //flashScreen(view);   flash screen
        //flashLight();        start flashing back flash light
        //flashLightOff();     stop flashing back flash light

        //k = 3;flashScreen(view);          stop flash screen (together with next line)
    }

    private void flashLight() {
        if (hasCameraFlash) {

            Handler handler = new Handler();

            Runnable runnable = new Runnable() {
                int i = 0;
                int j = 0;
                int delay = 1000;
                @Override
                public void run() {
                    Log.i("APP", "HELMM" + i);

                    if (isFlashOn) {
//                        flashLightBtn.setText("FLASH");
                        flashLightOff();
                        isFlashOn=false;
                        j = j + 1;
                    } else {
//                        flashLightBtn.setText("STOPPED");
                        flashLightOn();
                        isFlashOn=true;
                    }

                    if (j % 5 == 0) {
                        if (delay > 500) {
                            delay = delay - 100;
                        }
                    }

                    handler.postDelayed(this, delay);
                }
            };
            // Start the Runnable immediately
            handler.post(runnable);

            /*if (isFlashOn) {
                flashLightBtn.setText("FLASH");
                flashLightOff();
                isFlashOn=false;
            } else {
                flashLightBtn.setText("STOPPED");
                flashLightOn();
                isFlashOn=true;
            }*/
        } else {
            Toast.makeText(MainActivity.this, "Flash is not available on your device",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void flashLightOn() {
        CameraManager cameraManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        }

        try {
            String cameraId = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cameraId = cameraManager.getCameraIdList()[0];
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, true);
            }
        } catch (CameraAccessException e) {
        }
    }

    private void flashLightOff() {
        CameraManager cameraManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        }
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, false);
            }
        } catch (CameraAccessException e) {
        }
    }

    private void askMicPermission(String permission,int requestCode) {
        if (ContextCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{permission},requestCode);
        } else {
            // Already have permission
            startRecording();
        }
    }

    private void askCameraPermission(String permission,int requestCode) {
        if (ContextCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{permission},requestCode);

        }else {
            // Already have permission
            flashLight();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasCameraFlash = getPackageManager().
                            hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                    Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_LONG).show();
                    flashLight();
                } else {
                    Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_LONG).show();
                }
                break;
//            case LOCATION_REQUEST_CODE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    getLastLocation();
//                } else {
//                    Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_LONG).show();
//                }
//                break;
        }
    }

    /*public void flashLight(View view) {
        String myString = "010101010101";
        long blinkDelay =50; //Delay in ms
        for (int i = 0; i < myString.length(); i++) {
            if (myString.charAt(i) == '0') {
                params = Camera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(params);
                camera.startPreview();
                isFlashOn = true;
            } else {
                params = camera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(params);
                camera.stopPreview();
                isFlashOn = false;

            }
            try {
                Thread.sleep(blinkDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

//    @Override
//    public void onStart() {
//        super.onStart();
//        if (!checkPermissions()) {
//            startLocationPermissionRequest();
//        } else {
//            getLastLocation();
//        }
//    }
/*
    public void locate(View view) {
        if (!checkPermissions()) {
            startLocationPermissionRequest();
        } else {
            getLastLocation();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();

                            mLatitudeText.setText(String.format(Locale.ENGLISH, "%s: %f",
                                    "LAT",
                                    mLastLocation.getLatitude()));
                            mLongitudeText.setText(String.format(Locale.ENGLISH, "%s: %f",
                                    "LONG",
                                    mLastLocation.getLongitude()));
                        } else {
                            mLatitudeText.setText("error getting location");
                            mLongitudeText.setText("");
                        }
                    }
                });
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_REQUEST_CODE);
    }
*/
}
