package com.extravolume.sound.speakerbooster.vol.util;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;

import com.extravolume.sound.speakerbooster.R;


public class DNDModeChecker {
    public static boolean isDNDPermissionGranted(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        return TestLabUtils.isInTestLab(context) || Build.VERSION.SDK_INT < Build.VERSION_CODES.M || notificationManager.isNotificationPolicyAccessGranted();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void showDNDPermissionAlert(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.dnd_permission_title)
                .setMessage(context.getString(R.string.dnd_permission_message))
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    context.startActivity(intent);
                })
                .setCancelable(false)
                .create()
                .show();
    }
}
