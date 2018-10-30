package net.maxsmr.killapp.app;

import android.app.Application;
import android.net.Uri;

import net.maxsmr.commonutils.android.processmanager.ProcessManagerWrapper;
import net.maxsmr.commonutils.logger.BaseLogger;
import net.maxsmr.commonutils.logger.LogcatLogger;
import net.maxsmr.commonutils.logger.holder.BaseLoggerHolder;
import net.maxsmr.commonutils.shell.ShellWrapper;
import net.maxsmr.killapp.model.ConfigHolder;
import net.maxsmr.killapp.process.CustomProcessManagerWrapper;

import org.jetbrains.annotations.NotNull;

public class App extends Application {

    static {
        BaseLoggerHolder.initInstance(() -> new BaseLoggerHolder(false) {
            @Override
            protected BaseLogger createLogger(@NotNull Class<?> clazz) {
                if (clazz.equals(ShellWrapper.class)) {
                    return new BaseLogger.Stub();
                } else {
                    return new LogcatLogger(clazz.getSimpleName());
                }
            }
        });
    }

    private static ConfigHolder configHolder;

    @NotNull
    public static ConfigHolder getConfigHolder() {
        if (configHolder == null) {
            throw new IllegalStateException(ConfigHolder.class.getSimpleName() + " was not initialized");
        }
        return configHolder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ProcessManagerWrapper.initInstance(this, CustomProcessManagerWrapper::new);
        configHolder = new ConfigHolder(this, Uri.parse("asset_file://config.json"));
    }
}
