package com.extravolume.sound.speakerbooster.vol;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.preference.PreferenceManager;


import com.extravolume.sound.speakerbooster.vol.model.SettingsStorage;
import com.extravolume.sound.speakerbooster.vol.model.SoundProfileStorage;
import com.extravolume.sound.speakerbooster.vol.util.TestLabUtils;
import com.speakerboooster.apps.libs.speakerboost;


public class Sound extends Application {
    private speakerboost speakerboost;
    private SoundProfileStorage profileStorage;
    private SettingsStorage settingsStorage;

    public static speakerboost getVolumeControl(Context context) {
        return ((Sound) context.getApplicationContext()).getSpeakerboost();
    }

    public static SoundProfileStorage getSoundProfileStorage(Context context) {
        return ((Sound) context.getApplicationContext()).getProfileStorage();
    }

    public static SettingsStorage getSettingsStorage(Context context) {
        return ((Sound) context.getApplicationContext()).getSettingsStorage();
    }

    public speakerboost getSpeakerboost() {
        if (speakerboost == null) {
            speakerboost = new speakerboost(this, new Handler());
        }
        return speakerboost;
    }

    public SoundProfileStorage getProfileStorage() {
        if (profileStorage == null) {
            profileStorage = SoundProfileStorage.getInstance(this);
        }
        return profileStorage;
    }

    public SettingsStorage getSettingsStorage() {
        if (settingsStorage == null) {
            settingsStorage = new SettingsStorage(PreferenceManager.getDefaultSharedPreferences(this));
        }
        return settingsStorage;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        if (TestLabUtils.isInTestLab(this)) {
            //Toast.makeText(this, "in testlab", Toast.LENGTH_LONG).show();
        }
    }
}
