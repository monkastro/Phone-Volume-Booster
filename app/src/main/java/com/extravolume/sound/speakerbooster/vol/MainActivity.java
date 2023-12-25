package com.extravolume.sound.speakerbooster.vol;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import androidx.annotation.RequiresApi;
import androidx.core.internal.view.SupportMenu;
import androidx.navigation.ui.AppBarConfiguration;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.InterstitialAd;
import com.extravolume.sound.speakerbooster.Options;
import com.extravolume.sound.speakerbooster.R;
import com.extravolume.sound.speakerbooster.Setting;
import com.extravolume.sound.speakerbooster.SpeakerBoostService;
import com.extravolume.sound.speakerbooster.vol.data.Settings;
import com.extravolume.sound.speakerbooster.vol.data.SoundProfile;
import com.extravolume.sound.speakerbooster.vol.model.SoundProfileStorage;
import com.extravolume.sound.speakerbooster.vol.util.DNDModeChecker;
import com.extravolume.sound.speakerbooster.vol.util.DynamicShortcutManager;
import com.extravolume.sound.speakerbooster.vol.view.VolumeProfileView;
import com.extravolume.sound.speakerbooster.vol.view.VolumeSliderView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.speakerboooster.apps.libs.speakerboost;
import com.speakerboooster.apps.volumebooster.Preferance_Manager;
import com.speakerboooster.apps.volumebooster.Welcome_Activity;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ru.dimorinny.showcasecard.ShowCaseView;
import ru.dimorinny.showcasecard.position.TopRight;
import ru.dimorinny.showcasecard.radius.Radius;

import static com.extravolume.sound.speakerbooster.vol.EditProfileActivity.REQUEST_CODE_EDIT_PROFILE;
import static com.extravolume.sound.speakerbooster.vol.util.DNDModeChecker.isDNDPermissionGranted;
import static com.extravolume.sound.speakerbooster.vol.util.DNDModeChecker.showDNDPermissionAlert;
import static com.extravolume.sound.speakerbooster.vol.util.ProfileApplier.applyProfile;


public class MainActivity extends BaseActivity implements ServiceConnection {

    public static final String PROFILE_ID = "PROFILE_ID";
    private List<TypeListener> volumeListeners = new ArrayList<>();
    private SoundProfileStorage profileStorage;
    private boolean goingGoFinish = false;


    private static final String ADMOB_AD_UNIT_ID = "ca-app-pub-4509296003390665/6145844010";
    private CheckBox startVideoAdsMuted;
    private TextView videoStatus;
///test1
private static boolean DEBUG = true;


    private int SLIDER_MAX = 10000;
 private Settings settings;
    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();

    public AudioManager f22am;
    private SeekBar boostBar;
    private AppBarConfiguration mAppBarConfiguration;

    private Messenger messenger;
    private Switch switchCompat;
    public SharedPreferences options;
    private boolean startService = true;
    private boolean shouldLoad = true;

    public Setting setting;
    private boolean showVolume = true;
    private int versionCode;
    private SeekBar volumeBar;
    private ToggleButton toggleButton;
    ActionBar actionBar;
    ProgressDialog progressDialog;
    private UnifiedNativeAd nativeAd;

    public static void log(String s) {
        if (DEBUG) {
            Log.v("SpeakerBoost", s);
        }
    }

    private speakerboost.RingerModeChangeListener ringerModeSwitcher = (int mode) -> {
//        RingerModeSwitch ringerModeSwitch = findViewById(R.id.ringerMode);
        //ringerModeSwitch.setRingMode(mode);
    };

    public static Intent createOpenProfileIntent(Context context, SoundProfile profile) {
        Intent intent1 = new Intent(context.getApplicationContext(), MainActivity.class);
        intent1.setAction(Intent.ACTION_VIEW);
        intent1.putExtra(PROFILE_ID, profile.id);
        return intent1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mains);
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-4509296003390665~3276315707");
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        profileStorage = Sound.getSoundProfileStorage(this);
        buildUi();
        if (savedInstanceState == null) {
            if (handleIntent(getIntent())) {
                goingGoFinish = true;
                finish();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (!handleIntent(intent)) {
            super.onNewIntent(intent);
        }
    }

    private boolean handleIntent(Intent intent) {
        if (intent.hasExtra(PROFILE_ID)) {
            int profileId = intent.getIntExtra(PROFILE_ID, 0);
            try {
                SoundProfile profile = profileStorage.loadById(profileId);
                if (profile != null) {
                    applyProfile(control, profile);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            setIntent(null);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        //super.onSaveInstanceState(outState, outPersistentState);
    }

    private void renderVolumeTypesInNotificationWidget() {
        List<AudioType> allThings = AudioType.getAudioTypes(true);

        //TextView volumeTypesToShow = findViewById(R.id.types_to_show_in_profile);

       /*volumeTypesToShow.setOnClickListener(view -> {

            List<Integer> checked = new ArrayList<>(Arrays.asList(settings.volumeTypesToShow));

            CharSequence[] titles = new CharSequence[allThings.size()];
            boolean[] isCheckedArray = new boolean[allThings.size()];

            for (int i = 0; i < allThings.size(); i++) {
                titles[i] = getString(allThings.get(i).nameId);
                isCheckedArray[i] = checked.contains(allThings.get(i).audioStreamName);
            }

            new AlertDialog.Builder(this)
                    .setTitle(R.string.volume_types_in_widget)
                    .setMultiChoiceItems(titles, isCheckedArray, (dialogInterface, i, b) -> {
                        if (b) {
                            checked.add(allThings.get(i).audioStreamName);
                        } else {
                            checked.remove(Integer.valueOf(allThings.get(i).audioStreamName));
                        }
                    })
                    .setPositiveButton("save", (dialogInterface, i) -> {
                        setVolumeTypesToShowInWidget(checked.toArray(new Integer[0]));
                        startSoundService();
                    })
                    .show();
        });*/
    }

    @Override
    protected void setExtendedVolumesEnabled(boolean isEnabled) {
        super.setExtendedVolumesEnabled(isEnabled);
        renderProfileItems();
    }

    private void renderProfileItems() {
        View title = findViewById(R.id.audio_types_holder_title);
        ViewGroup titlesGroup = findViewById(R.id.linearLayout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            titlesGroup.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        }

        int indexOfTitle = titlesGroup.indexOfChild(title);

        List<AudioType> audioTypes = AudioType.getAudioTypes(true);

        if (!Boolean.TRUE.equals(title.getTag())) {
            for (int i = 0; i < audioTypes.size(); i++) {
                AudioType type = audioTypes.get(i);

                final VolumeSliderView volumeSliderView = new VolumeSliderView(this);

                volumeSliderView.setTag(type.audioStreamName);
                titlesGroup.addView(volumeSliderView, indexOfTitle + i + 1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                volumeSliderView.setVolumeName(getString(type.nameId));
                volumeSliderView.setMaxVolume(control.getMaxLevel(type.audioStreamName));
                volumeSliderView.setMinVolume(control.getMinLevel(type.audioStreamName));
                volumeSliderView.setCurrentVolume(control.getLevel(type.audioStreamName));


                final TypeListener volumeListener = new TypeListener(type.audioStreamName) {
                    @Override
                    public void onChangeIndex(int audioType, int currentLevel, int max) {
                        if (currentLevel < control.getMinLevel(type)) {
                            volumeSliderView.setCurrentVolume(control.getMinLevel(type));
                        } else {
                            volumeSliderView.setCurrentVolume(currentLevel);
                        }
                    }
                };

                volumeListeners.add(volumeListener);

                volumeSliderView.setListener((volume, fromUser) -> {
                    if (fromUser) {
                        requireChangeVolume(type, volume);
                    }
                });
            }
            title.setTag(Boolean.TRUE);
        }

        for (AudioType audioExtendedType : AudioType.getAudioExtendedTypes()) {
            titlesGroup.findViewWithTag(audioExtendedType.audioStreamName).setVisibility(isExtendedVolumesEnabled() ? View.VISIBLE : View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void buildUi() {


        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .threshold(3)
                .session(4)
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {

                    }
                }).build();

        ratingDialog.show();
        // here put loading dialogue
        ProgressDialog   progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("\uD83D\uDE24 Ads are irritating ...\uD83D\uDE24"); // Setting Message
        progressDialog.setTitle("\uD83E\uDD11 Sorry for ads  \uD83E\uDD11"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }).start();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                // mInterstitial.show();
                //buttons[inew][jnew].setBackgroundColor(Color.BLACK);
                Admob.createLoadInterstitial(getApplicationContext(),null);
                //Toast.makeText(this, "Admob Interstial loades", Toast.LENGTH_SHORT).show();
                log("admob interstial ads loaded");
            }
        }, 3000);



        //preference button
        startVideoAdsMuted = findViewById(R.id.cb_start_muted);
        refreshAd();

        Button pre_btn=(Button)findViewById(R.id.booster_settings);
        pre_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Admob.createLoadInterstitial(getApplicationContext(), null)== true){
                    startActivity(new Intent(MainActivity.this, Options.class));
                    log("admob interstial is loaded onclick option button");
                }

                startActivity(new Intent(MainActivity.this, Options.class));
            }
        });

        ///test1
        this.options = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            this.versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            this.versionCode = 0;
        }
        this.f22am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        assert this.f22am != null;
//         this.f22am.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        this.setting = new Setting(this, false);
        this.options.edit().putBoolean(Options.PREF_VOLUME,
                this.options.getBoolean(Options.PREF_VOLUME, Options.defaultShowVolume())).apply();
        this.boostBar = (SeekBar) findViewById(R.id.boost);








        Button rate_us=(Button)findViewById(R.id.rate_us);
        rate_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                   MainActivity.log("error while launching application");
                }
            }
        });


        Button tutorial_btn =(Button)findViewById(R.id.tutorial_btn);
        tutorial_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // We normally won't show the welcome slider again in real app
                // but this is for testing
                Preferance_Manager prefManager = new Preferance_Manager(getApplicationContext());

                // make first time launch TRUE
                prefManager.setFirstTimeLaunch(true);

                startActivity(new Intent(MainActivity.this, Welcome_Activity.class));
                finish();
            }
        });
        TextView activate_state =(TextView)findViewById(R.id.activate_state);

        //Switch s = findViewById(R.id.dark_theme_switcher);

        //s.setChecked(isDarkTheme());
        //s.setOnCheckedChangeListener((buttonView, isChecked) -> setThemeAndRecreate(isChecked));

        /*findViewById(R.id.rate_app).setOnClickListener(v -> {
            try {
                IntentHelper.goToMarket(this);
            } catch (Throwable e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/

        renderProfileItems();

        //Switch s2 = findViewById(R.id.extended_volumes);
        //s2.setChecked(isExtendedVolumesEnabled());
        //s2.setOnCheckedChangeListener((buttonView, isChecked) -> setExtendedVolumesEnabled(isChecked));


        //findViewById(R.id.go_to_settings).setOnClickListener(v -> IntentHelper.goToVolumeSettings(this));

        //findViewById(R.id.new_profile).setOnClickListener(v -> startActivityForResult(new Intent(MainActivity.this, EditProfileActivity.class), REQUEST_CODE_NEW_PROFILE));


        //RingerModeSwitch ringerModeSwitch = findViewById(R.id.ringerMode);
       // ringerModeSwitch.setRingMode(control.getRingerMode());
        //ringerModeSwitch.setRingSwitcher(control::requestRingerMode);
        control.addOnRingerModeListener(ringerModeSwitcher);
        //ringerModeSwitch.setVisibility(View.GONE);

        Switch notificationSwitch = findViewById(R.id.notification_widget);

        notificationSwitch.setChecked(isNotificationWidgetEnabled());

        //Switch profilesSwitch = findViewById(R.id.notification_widget_profiles);

        //profilesSwitch.setChecked(settings.showProfilesInNotification);

        /*profilesSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            setNotificationProfiles(isChecked);
            startSoundService();
        });*/
       // TextView volumeTypesToShow = findViewById(R.id.types_to_show_in_profile);

        renderVolumeTypesInNotificationWidget();

        //profilesSwitch.setVisibility(isNotificationWidgetEnabled() ? View.VISIBLE : View.GONE);
        //volumeTypesToShow.setVisibility(isNotificationWidgetEnabled() ? View.VISIBLE : View.GONE);

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setNotificationWidgetEnabled(isChecked);
           // profilesSwitch.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            //volumeTypesToShow.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked) {

                startSoundService();
                activate_state.setText("Boost Is Activated");
                //show ads when booster is activated

            } else {

                stopSoundService();
                activate_state.setText("Boost is Deactivated");


            }
        });


        try {
            renderProfiles();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int i = 0;
        if (keyCode == 24) {
            AudioManager audioManager = this.f22am;
            if (!this.showVolume) {
                i = 1;
            }
            audioManager.adjustStreamVolume(3, 1, i);

            return true;
        } else if (keyCode != 25) {
            return super.onKeyDown(keyCode, event);
        } else {
            AudioManager audioManager2 = this.f22am;
            if (!this.showVolume) {
                i = 1;
            }
            audioManager2.adjustStreamVolume(3, -1, i);

            return true;
        }
    }
    private void reloadSettings() {
        sendMessage(2, 0, 0);
    }

    private void warning() {
        this.setting.boostValue = 0;
        this.setting.save(this.options);
        this.boostBar.setProgress(0);
        reloadSettings();
        this.options.edit().putInt(Options.PREF_WARNED_LAST_VERSION, this.versionCode).apply();
    }

    private void versionUpdate() {
        if (this.options.getInt(Options.PREF_WARNED_LAST_VERSION, 0) != this.versionCode) {
            warning();
        }
    }
    public void updateService(boolean value) {
        if (value) {
            log("restartService");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                restartService(true);
            }
            return;
        }
        log("stopService");
        stopService();
        updateNotification();
    }


    public void updateService() {
        log("needService = " + this.setting.needService());
        updateService(this.setting.needService());
    }


    public void updateBoostText(int progress) {
        // Get instance of Vibrator from current Context
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

// Vibrate for 400 milliseconds
        TextView textView =(TextView)findViewById(R.id.boost_value) ;
        textView.setText("" + (((progress * 100) + (this.SLIDER_MAX / 2)) / this.SLIDER_MAX) + "%");

        v.vibrate(100);
    }

    public void setupEqualizer() {
        log("setupEqualizer");
        this.boostBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                boolean z;
                boolean z2 = true;
                MainActivity.log("progress changed");
                if (fromUser) {
                    int oldBoost = MainActivity.this.setting.boostValue;
                    MainActivity.this.setting.boostValue = MainActivity.this.fromSlider(progress, 0, 1500);
                    MainActivity.log("setting " + MainActivity.this.setting.boostValue);
                    MainActivity.this.setting.save(MainActivity.this.options);
                    if (MainActivity.this.setting.boostValue == 0) {
                        z = true;
                    } else {
                        z = false;
                    }
                    if (oldBoost != 0) {
                        z2 = false;
                    }
                    if (z != z2) {
                        MainActivity.this.updateService();
                    } else {
                        MainActivity.this.sendMessage(2, 0, 0);
                    }
                }
                MainActivity.this.updateBoostText(progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {


                // show interstial ads when stop tracking slider



            }
        });
        int i = this.options.getInt(Options.PREF_BOOST, 0);
        Setting settings2 = this.setting;
        int progress = toSlider(i, 0, 1500);
        this.boostBar.setProgress(progress);
        updateBoostText(progress);
    }

    /* access modifiers changed from: private */
    public int fromSlider(int value, int min, int max) {
        return ((((this.SLIDER_MAX - value) * min) + (max * value)) + (this.SLIDER_MAX / 2)) / this.SLIDER_MAX;
    }

    private int toSlider(int value, int min, int max) {
        return (((value - min) * this.SLIDER_MAX) + ((max - min) / 2)) / (max - min);
    }



    public void onResume() {
        super.onResume();
        if (goingGoFinish) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DynamicShortcutManager.setShortcuts(this, profileStorage.loadAll());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (isNotificationWidgetEnabled() && isDNDPermissionGranted(this)) {
            startSoundService();
        }
        versionUpdate();
        this.setting.load(this.options);
        log("loaded boost = " + this.setting.boostValue);
        int maxBoost = Options.getMaximumBoost(this.options);
        this.boostBar.setMax((this.SLIDER_MAX * maxBoost) / 100);
        int i = this.setting.boostValue;
        Setting settings2 = this.setting;
        if (i > (maxBoost * 1500) / 100) {
            Setting settings3 = this.setting;
            Setting settings4 = this.setting;
            settings3.boostValue = (maxBoost * 1500) / 100;
            this.setting.save(this.options);
        }
        setupEqualizer();
        updateService();
        updateNotification();

    }

    private void setSeekBarColor(SeekBar currentSeekBar) {
        currentSeekBar.getProgressDrawable().setColorFilter(Color.parseColor("#ff7f00"), PorterDuff.Mode.SRC_IN);
        currentSeekBar.getThumb().setColorFilter(Color.parseColor("#ff7f00"), PorterDuff.Mode.SRC_IN);
    }

    public void onPause() {
        super.onPause();
        if (this.messenger != null) {
            log("unbind");
            unbindService(this);
            this.messenger = null;
        }
    }

    public void setNotification(Context c, NotificationManager nm, Setting s) {
        PendingIntent pIntent = PendingIntent.getActivity(c, 0, new Intent(c, MainActivity.class), 0);
        int notificationIcon = s.somethingOn() ? R.drawable.equalizeron : R.drawable.equalizeroff;


    }

    private String setNotificationChannel(NotificationManager notificationManager) {
        Intent startupIntent = new Intent(getApplicationContext(),MainActivity.class);
        startupIntent.setAction(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_BROWSABLE);
        startupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        String id = "my_channel_01";
        if (Build.VERSION.SDK_INT >= 26) {
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

    private void updateNotification() {
        updateNotification(this, this.options, (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE), this.setting);
    }

    public void updateNotification(Context c, SharedPreferences options2, NotificationManager nm, Setting s) {
        log("notify " + Options.getNotify(options2));
        switch (Options.getNotify(options2)) {
            case 0:
                if (s.needService()) {
                    setNotification(c, nm, s);
                    return;
                }
                log("trying to cancel notification");
                nm.cancelAll();
                return;
            case 1:
                setNotification(c, nm, s);
                return;
            default:
                setNotification(c, nm, s);
                return;
        }
    }


    public void stopService() {
        log("stop service");
        if (this.messenger != null) {
            unbindService(this);
            this.messenger = null;
        }
        stopService(new Intent(this, SpeakerBoostService.class));
    }


    public void saveSettings() {
    }


    public void bind() {
        log("bind");
        bindService(new Intent(this, SpeakerBoostService.class), this, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void restartService(boolean bind) {
        stopService();
        saveSettings();
        log("starting service");
        startForegroundService(new Intent(this, SpeakerBoostService.class));
        if (bind) {
            bind();
        }
    }

    public void sendMessage(int n, int arg1, int arg2) {
        if (this.messenger != null) {
            try {
                log("message " + n + " " + arg1 + " " + arg2);
                this.messenger.send(Message.obtain(null, n, arg1, arg2));
            } catch (RemoteException e) {
            }
        }
    }

    public void onServiceConnected(ComponentName classname, IBinder service) {
        log("connected");
        this.messenger = new Messenger(service);
    }

    public void onServiceDisconnected(ComponentName name) {
        log("disconnected");
        this.messenger = null;
    }

    @Override
    public void onBindingDied(ComponentName name) {

    }

    public void optionsClick(View v) {
        startActivity(new Intent(this, Options.class));
    }

    public void exitApp(View view) {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main2, menu);
        return true;
    }
    public boolean onContextItemSelected(MenuItem item) {
        onOptionsItemSelected(item);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.setting:
                Intent intent=new Intent(MainActivity.this, Options.class);
                startActivity(intent);
                return true;
            case R.id.reward:
                new ShowCaseView.Builder(MainActivity.this)
                        .withTypedPosition(new TopRight())
                        .withTypedRadius(new Radius(186F))
                        .withContent("Important Notice :- On Some Device Background Process not Supporting, Use Floating mode to avoid background crash. Thank You")
                        .build()
                        .show(MainActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void stopSoundService() {
        Intent i = SoundService.getStopIntent(this);
        startService(i);
    }

    private void startSoundService() {
        Intent i = SoundService.getIntentForForeground(this, settings);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(i);
        } else {
            startService(i);
        }
    }

   /* @Override
    protected void onResume() {
        super.onResume();
        if (goingGoFinish) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DynamicShortcutManager.setShortcuts(this, profileStorage.loadAll());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (isNotificationWidgetEnabled() && isDNDPermissionGranted(this)) {
            startSoundService();
        }

    }*/



    private void renderProfile(final SoundProfile profile) {
        final LinearLayout profiles = findViewById(R.id.profile_list);
        final VolumeProfileView view = new VolumeProfileView(this);
        String tag = "profile_" + profile.id;
        profiles.removeView(profiles.findViewWithTag(tag));
        view.setTag(tag);


        view.setProfileTitle(profile.name);
        view.setOnActivateClickListener(() -> applyProfile(control, profile));
        view.setOnEditClickListener(() -> {
            profileStorage.removeProfile(profile.id);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DynamicShortcutManager.removeShortcut(this, profile);
            }
            profiles.removeView(view);
        });
        view.setOnClickListener(view1 -> startActivityForResult(EditProfileActivity.getIntentForEdit(this, profile), REQUEST_CODE_EDIT_PROFILE));
        profiles.addView(view,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );
    }

    private void renderProfiles() throws JSONException {
        LinearLayout profiles = findViewById(R.id.profile_list);
        profiles.removeAllViews();

        for (final SoundProfile profile : profileStorage.loadAll()) {
            renderProfile(profile);
        }

    }

    private void requireChangeVolume(AudioType audioType, int volume) {
        try {
            control.setVolumeLevel(audioType.audioStreamName, volume);
        } catch (Throwable throwable) {
            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            throwable.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!DNDModeChecker.isDNDPermissionGranted(this)) {
            showDNDPermissionAlert(this);
        }
        for (TypeListener listener : volumeListeners)
            control.registerVolumeListener(listener.type, listener, true);
    }

    @Override
    protected void recreateActivity() {
        Intent intent = new Intent(this, this.getClass());
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(intent);

    }

    @Override
    protected void onStop() {
        super.onStop();
        for (TypeListener volumeListener : volumeListeners)
            control.unRegisterVolumeListener(volumeListener.type, volumeListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        volumeListeners.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_EDIT_PROFILE:
            case EditProfileActivity.REQUEST_CODE_NEW_PROFILE: {
                if (resultCode == Activity.RESULT_OK) {
                    HashMap<Integer, Integer> volumes = (HashMap<Integer, Integer>) data.getSerializableExtra("volumes");
                    String name = data.getStringExtra("name");
                    SoundProfile profile;
                    if (requestCode == EditProfileActivity.REQUEST_CODE_NEW_PROFILE) {
                        profile = profileStorage.addProfile(name, volumes);
                    } else {
                        int id = data.getIntExtra("id", -1);
                        profile = new SoundProfile();
                        profile.id = id;
                        profile.name = name;
                        profile.settings = volumes;
                        profileStorage.saveProfile(profile);
                    }
                    renderProfile(profile);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (DynamicShortcutManager.isPinnedShortcutSupported(this)) {
                            DynamicShortcutManager.installPinnedShortcut(this, profile);
                        }
                    }
                }
                break;
            }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    static abstract class TypeListener implements speakerboost.VolumeListener {
        final int type;

        TypeListener(int type) {
            this.type = type;
        }
    }
    /**
     * Populates a {@link UnifiedNativeAdView} object with data from a given
     * {@link UnifiedNativeAd}.
     *
     * @param nativeAd the object containing the ad's assets
     * @param adView          the view to be populated
     */
    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view.
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {
            videoStatus.setText(String.format(Locale.getDefault(),
                    "Video status: Ad contains a %.2f:1 video asset.",
                    vc.getAspectRatio()));

            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.

                   // videoStatus.setText("Video status: Video playback has ended.");
                    super.onVideoEnd();
                }
            });
        }
    }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     *
     */
    private void refreshAd() {


        AdLoader.Builder builder = new AdLoader.Builder(this, ADMOB_AD_UNIT_ID);

        builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    // OnUnifiedNativeAdLoadedListener implementation.
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // If this callback occurs after the activity is destroyed, you must call
                        // destroy and return or you may get a memory leak.
                        boolean isDestroyed = false;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            isDestroyed = isDestroyed();
                        }
                        if (isDestroyed || isFinishing() || isChangingConfigurations()) {
                            unifiedNativeAd.destroy();
                            return;
                        }
                        // You must call destroy on old ads when you are done with them,
                        // otherwise you will have a memory leak.
                        if (nativeAd != null) {
                            nativeAd.destroy();
                        }
                        nativeAd = unifiedNativeAd;
                        FrameLayout frameLayout = findViewById(R.id.fl_adplaceholder);
                        UnifiedNativeAdView adView =
                                (UnifiedNativeAdView) getLayoutInflater()
                                        .inflate(R.layout.nativeads_layout, null);
                        populateUnifiedNativeAdView(unifiedNativeAd, adView);
                        frameLayout.removeAllViews();
                        frameLayout.addView(adView);
                    }
                });

        VideoOptions videoOptions =
                new VideoOptions.Builder().setStartMuted(startVideoAdsMuted.isChecked()).build();

        NativeAdOptions adOptions =
                new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader =
                builder
                        .withAdListener(
                                new AdListener() {
                                    @Override
                                    public void onAdFailedToLoad(LoadAdError loadAdError) {

                                        String error =
                                                String.format(
                                                        "domain: %s, code: %d, message: %s",
                                                        loadAdError.getDomain(),
                                                        loadAdError.getCode(),
                                                        loadAdError.getMessage());
                                        Toast.makeText(
                                                MainActivity.this,
                                                "Failed to load native ad with error " + error,
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                })
                        .build();

        adLoader.loadAd(new AdRequest.Builder().build());

//        videoStatus.setText("");


    }







}
