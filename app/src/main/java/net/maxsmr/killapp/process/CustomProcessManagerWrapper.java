package net.maxsmr.killapp.process;

import android.content.Context;
import android.os.Build;

import net.maxsmr.commonutils.android.processmanager.AbstractProcessManager;
import net.maxsmr.commonutils.android.processmanager.DefaultProcessManager;
import net.maxsmr.commonutils.android.processmanager.ProcessManagerWrapper;
import net.maxsmr.commonutils.android.processmanager.shell.BusyboxPsProcessManager;
import net.maxsmr.commonutils.android.processmanager.shell.BusyboxTopProcessManager;
import net.maxsmr.commonutils.android.processmanager.shell.PsProcessManager;
import net.maxsmr.commonutils.android.processmanager.shell.ToolboxPsProcessManager;
import net.maxsmr.commonutils.android.processmanager.shell.TopProcessManager;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

public class CustomProcessManagerWrapper extends ProcessManagerWrapper {

    public CustomProcessManagerWrapper(@NotNull Context context) {
        super(context);
    }

    @NotNull
    @Override
    protected Set<AbstractProcessManager> getManagersByPriority(@NotNull Context context) {
        @NotNull Set<AbstractProcessManager> managers = new LinkedHashSet<>();
        final DefaultProcessManager defaultProcessManager = new DefaultProcessManager(context);
        final boolean isKitKat = Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT;
        if (isKitKat) {
            managers.add(defaultProcessManager);
        }
        managers.add(new ToolboxPsProcessManager(context));
        managers.add(new PsProcessManager(context));
        managers.add(new BusyboxPsProcessManager(context));
        managers.add(new TopProcessManager(context));
        managers.add(new BusyboxTopProcessManager(context));
        if (!isKitKat) {
            managers.add(defaultProcessManager);
        }
        return managers;
    }
}
