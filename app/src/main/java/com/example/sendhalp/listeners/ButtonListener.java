package com.example.sendhalp.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

public class ButtonListener extends BroadcastReceiver {
    private static final String CLASS_NAME = "ButtonListener";

    public ButtonListener() {
        super();
        Log.i(CLASS_NAME, "Initialised");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(CLASS_NAME, "sth pressed");

        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (KeyEvent.KEYCODE_VOLUME_DOWN == event.getKeyCode()) {

                Log.i(CLASS_NAME, "Down pressed");
            }

            if (KeyEvent.KEYCODE_VOLUME_UP == event.getKeyCode()) {
                Log.i(CLASS_NAME, "Up pressed");
            }
        }
    }
}
