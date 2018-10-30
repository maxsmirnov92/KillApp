package net.maxsmr.killapp.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import net.maxsmr.commonutils.android.processmanager.ProcessManagerHolder;
import net.maxsmr.commonutils.data.CompareUtils;
import net.maxsmr.commonutils.logger.BaseLogger;
import net.maxsmr.commonutils.logger.holder.BaseLoggerHolder;
import net.maxsmr.commonutils.shell.RootShellCommands;
import net.maxsmr.killapp.BuildConfig;
import net.maxsmr.killapp.model.Config;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EndCallReceiver extends BroadcastReceiver {

    private static final BaseLogger logger = BaseLoggerHolder.getInstance().getLogger(EndCallReceiver.class);

    private static final String ACTION = TelephonyManager.ACTION_PHONE_STATE_CHANGED;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void onReceive(Context context, Intent intent) {

        Config config = App.getConfigHolder().getConfig();

        if (config == null) {
            logger.e("Cannot react on " + ACTION + ": " + Config.class.getSimpleName() + " is not initialized");
            return;
        }

        if (!config.hasAppsToKill()) {
            return;
        }

        if (ACTION.equals(intent.getAction())) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (TextUtils.isEmpty(state)) {
                logger.e("Cannot react on " + ACTION + ": " + " no phone state specified");
                return;
            }

            long delay = config.getKillDelay();

            if (delay < 0) {
                delay = 0;
            }

            if (CompareUtils.stringsEqual(state, TelephonyManager.EXTRA_STATE_IDLE, false)) {
                handler.postDelayed(() -> executor.execute(() -> {
                    if (isCallActive(context)) {
                        return;
                    }
                    killRestrictionPackages(config);
                }), delay);
            }
        }
    }

    private void killRestrictionPackages(@NotNull Config config) {

        final List<String> restrictionPackages = config.getRestrictionPackages();

        if (BuildConfig.DEBUG) {

            for (String packageName : restrictionPackages) {
                Map<Integer, Boolean> statusMap = RootShellCommands.killProcessesByNameWithStatus(packageName,
                        ProcessManagerHolder.getInstance().getProcessManager(),
                        config.isIncludeSystemPackages());

                if (statusMap == null) {
                    logger.i("Process(es) '" + packageName + "' not found");

                } else {

                    boolean killResult = true;
                    for (Boolean status : statusMap.values()) {
                        if (status == null || !status) {
                            killResult = false;
                            break;
                        }
                    }

                    if (killResult) {
                        logger.i("Process(es) '" + packageName + "' was killed successfully");
                    } else {
                        logger.e("Process(es) '" + packageName + "' kill failed!");
                    }
                }
            }

        } else {
            // not calling getProcesses() on each package iteration
            RootShellCommands.killProcessesByNames(restrictionPackages, ProcessManagerHolder.getInstance().getProcessManager(),
                    config.isIncludeSystemPackages());
        }
    }

    // TODO move
    public static boolean isCallActive(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (manager == null) {
            throw new RuntimeException(AudioManager.class.getSimpleName() + " is null");
        }
        return manager.getMode() == AudioManager.MODE_IN_CALL;
    }
}
