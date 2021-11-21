package com.evstapps.familysoundboard;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public class EVSTRingtoneManager {

    private Context ctx;

    EVSTRingtoneManager(Context ctx) {
        this.ctx = ctx;
    }

    public void SetAsRingtoneOrNotification(String assetFilePath, int type) {
        if (!Settings.System.canWrite(ctx)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            ctx.startActivity(intent);
            return;
        }
        try {
            String externalFilePath;
            if (type == RingtoneManager.TYPE_RINGTONE) {
                externalFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES) + "/EVST_Ringtone.mp3";
            } else {
                externalFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS) + "/EVST_Notification.mp3";
            }
            AssetFileDescriptor afd = ctx.getAssets().openFd(assetFilePath);
            FileChannel inChannel = afd.createInputStream().getChannel();
            FileChannel outChannel = new FileOutputStream(externalFilePath).getChannel();
            inChannel.transferTo(afd.getStartOffset(), afd.getLength(), outChannel);
            inChannel.close();
            outChannel.close();

            File externalFile = new File(externalFilePath);
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Media.IS_ALARM, false);
            values.put(MediaStore.Audio.Media.IS_MUSIC, false);
            if (type == RingtoneManager.TYPE_RINGTONE) {
                values.put(MediaStore.MediaColumns.TITLE, "EVST_Ringtone");
                values.put(MediaStore.Audio.Media.ARTIST, "EVST_Ringtone");
                values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
            } else {
                values.put(MediaStore.MediaColumns.TITLE, "EVST_Notification");
                values.put(MediaStore.Audio.Media.ARTIST, "EVST_Notification");
                values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
                values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
            }

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Uri newUri = ctx.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
                OutputStream os = ctx.getContentResolver().openOutputStream(newUri);
                int size = (int) externalFile.length();
                byte[] bytes = new byte[size];
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(externalFile));
                buf.read(bytes, 0, bytes.length);
                buf.close();
                os.write(bytes);
                os.close();
                os.flush();
                RingtoneManager.setActualDefaultRingtoneUri(ctx, type, newUri);
            } else {
                values.put(MediaStore.MediaColumns.DATA, externalFile.getAbsolutePath());
                Uri uri = MediaStore.Audio.Media.getContentUriForPath(externalFile.getAbsolutePath());
                ctx.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + externalFile.getAbsolutePath() + "\"", null);
                Uri newUri = ctx.getContentResolver().insert(uri, values);
                RingtoneManager.setActualDefaultRingtoneUri(ctx, type, newUri);
                ctx.getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(externalFile.getAbsolutePath()), values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
