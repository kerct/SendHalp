package com.example.sendhalp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Handler;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.os.Build;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {

    Button flashLightBtn;
    private final int CAMERA_REQUEST_CODE = 2;
    boolean hasCameraFlash = false;
    private boolean isFlashOn = false;
    int k = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hasCameraFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        flashLightBtn = findViewById(R.id.Flash);

        flashLightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askPermission(Manifest.permission.CAMERA, CAMERA_REQUEST_CODE);

            }
        });
    }

    public void playAlarm(View view) {
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

    public void flashScreen(View view) {
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
                        flashLightBtn.setText("FLASH");
                        flashLightOff();
                        isFlashOn=false;
                        j = j + 1;
                    } else {
                        flashLightBtn.setText("STOPPED");
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

    private void askPermission(String permission,int requestCode) {
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
        }

    }

}