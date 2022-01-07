package com.example.gpstracker;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class Controller extends DeviceAdminReceiver {
    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        Log.d("lock","Activated driving mode");
    }

    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        Log.d("lock","Deactivated driving mode");
    }
}
