package com.extravolume.sound.speakerbooster;

import android.annotation.SuppressLint;
import android.app.Notification.Builder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.AudioEffect.Descriptor;
import android.media.audiofx.LoudnessEnhancer;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.core.internal.view.SupportMenu;

import com.extravolume.sound.speakerbooster.vol.MainActivity;

public class SpeakerBoostService extends Service {
  protected boolean interruptReader;
  private final Messenger messenger = new Messenger(new IncomingHandler());

  public SharedPreferences options;

  public Setting settings;
  private static final String TAG = SpeakerBoostService.class.getSimpleName();

  private static final int SERVICE_STARTED_NOTIFICATION_ID = -1;

  private long f23t0;

  public class IncomingHandler extends Handler {
    public static final int MSG_OFF = 0;
    public static final int MSG_ON = 1;
    public static final int MSG_RELOAD_SETTINGS = 2;

    public IncomingHandler() {
    }

    public void handleMessage(Message m) {
      MainActivity.log("Message: " + m.what);
      switch (m.what) {
        case 2:
          SpeakerBoostService.this.settings.load(SpeakerBoostService.this.options);
          SpeakerBoostService.this.settings.setEqualizer();
          return;
        default:
          super.handleMessage(m);
          return;
      }
    }
  }

  public IBinder onBind(Intent arg0) {
    return this.messenger.getBinder();
  }

  @SuppressLint({"NewApi"})
  public void onCreate() {
    this.f23t0 = System.currentTimeMillis();
    MainActivity.log("Creating service at " + this.f23t0);
    this.options = PreferenceManager.getDefaultSharedPreferences(this);
    this.settings = new Setting(this, true);
    this.settings.load(this.options);
    if (!this.settings.haveEqualizer()) {
      this.settings.boostValue = 0;
      this.settings.save(this.options);
      if (19 <= VERSION.SDK_INT) {
        Boolean have = Boolean.valueOf(false);
        Descriptor[] queryEffects = AudioEffect.queryEffects();
        int length = queryEffects.length;
        int i = 0;
        while (true) {
          if (i >= length) {
            break;
          } else if (queryEffects[i].uuid.equals(LoudnessEnhancer.EFFECT_TYPE_LOUDNESS_ENHANCER)) {
            have = Boolean.valueOf(true);
            break;
          } else {
            i++;
          }
        }
        if (!have.booleanValue()) {
          Toast.makeText(this, "Your device doesn't support the LoudnessEnhancer effect that Speaker Booster Full Pro needs.", Toast.LENGTH_LONG).show();
        } else {
          Toast.makeText(this, "Error: Try later or reboot", Toast.LENGTH_LONG).show();
        }
      } else {
        Toast.makeText(this, "Error: Try later or reboot", Toast.LENGTH_LONG).show();
      }
      MainActivity.log("Error setting up equalizer");
    } else {
      MainActivity.log("Success setting up equalizer");
    }
    PendingIntent pIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

    if (VERSION.SDK_INT >= 26) {
      startForeground(1, new Builder(this, setNotificationChannel()).setContentTitle(getString(R.string.app_name)).setSmallIcon(R.drawable.equalizeron).setContentIntent(pIntent).setAutoCancel(true).build());
    } else {
      startForeground(1, new Builder(this).setContentTitle(getString(R.string.app_name)).setSmallIcon(R.drawable.equalizeron).setContentIntent(pIntent).setAutoCancel(true).build());
    }
    if (this.settings.isEqualizerActive()) {
      this.settings.setEqualizer();
    } else {
      this.settings.disableEqualizer();
    }
  }

  private String setNotificationChannel() {
    String id = "my_channel_01";
    if (VERSION.SDK_INT >= 26) {
      NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
      NotificationChannel mChannel = new NotificationChannel(id, "Speaker booster channel", NotificationManager.IMPORTANCE_LOW);
      mChannel.setDescription("Speaker booster loudness increasing");
      mChannel.enableLights(true);
      mChannel.setLightColor(SupportMenu.CATEGORY_MASK);
      mChannel.enableVibration(true);
      mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
      notificationManager.createNotificationChannel(mChannel);

    }
    return id;
  }

  public void onDestroy() {
    super.onDestroy();
    this.settings.load(this.options);
    MainActivity.log("disabling equalizer");
    this.settings.destroyEqualizer();
    MainActivity.log("Destroying service");
  }

  public void onStart(Intent intent, int flags) {
  }

  public int onStartCommand(Intent intent, int flags, int startId) {
    onStart(intent, flags);
    return Service.START_STICKY;
  }
}
