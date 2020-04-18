package com.miaxis.face.manager;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.miaxis.face.app.App;

public class ToastManager {

    private static Toast toast;
    private static Handler handler = new Handler(Looper.getMainLooper());

    public static void toast(String message) {
        handler.post(() -> {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(App.getInstance().getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.show();
        });
    }

}
