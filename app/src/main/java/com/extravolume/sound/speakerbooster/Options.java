package com.extravolume.sound.speakerbooster;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class Options extends PreferenceActivity {
  public static final int NOTIFY_ALWAYS = 1;
  public static final int NOTIFY_AUTO = 0;
  public static final String PREF_BOOST = "boost2";
  public static final String PREF_MAXIMUM_BOOST = "maximumBoost2";
  public static final String PREF_MAXIMUM_BOOST_OLD = "maximumBoost";
  public static final String PREF_NOTIFY = "notification";
  public static final String PREF_NO_WARN = "noWarn";
  public static final String PREF_SHAPE = "shape";
  public static final String PREF_VOLUME = "volumeControl";
  public static final String PREF_WARNED_LAST_VERSION = "warnedLastVersion";

  public static class MyPreferenceFragment extends PreferenceFragment {
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.options);
    }
  }

  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    getFragmentManager().beginTransaction().replace(16908290, new MyPreferenceFragment()).commit();
  }

  public void onResume() {
    super.onResume();
  }

  public void onStop() {
    super.onStop();
  }

  public static boolean isKindle() {
    return Build.MODEL.equalsIgnoreCase("Kindle Fire");
  }

  public static int getNotify(SharedPreferences options) {
    int n = Integer.parseInt(options.getString(PREF_NOTIFY, "1"));
    switch (n) {
      case 0:
      case 1:
        return n;
      default:
        return 0;
    }
  }

  public static boolean defaultShowVolume() {
    return isKindle();
  }

  public static int getMaximumBoost(SharedPreferences options) {
    try {
      int old = Integer.parseInt(options.getString(PREF_MAXIMUM_BOOST_OLD, "-1"));
      if (old < 0) {
        return Integer.parseInt(options.getString(PREF_MAXIMUM_BOOST, "60"));
      }
      options.edit().putString(PREF_MAXIMUM_BOOST_OLD, "-1").commit();
      if (old >= 60) {
        return 60;
      }
      return old;
    } catch (NumberFormatException e) {
      return 60;
    }
  }
}
