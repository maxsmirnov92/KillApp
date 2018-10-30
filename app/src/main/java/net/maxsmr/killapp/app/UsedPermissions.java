package net.maxsmr.killapp.app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import net.maxsmr.permissionchecker.PermissionUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;

public interface UsedPermissions {

    String READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;

    String[] PERMISSIONS = {
            READ_PHONE_STATE
    };

    class P {

        public static boolean hasPermissions(@NotNull Context context) {
            boolean has = true;
            for (String p : UsedPermissions.PERMISSIONS) {
                if (!PermissionUtils.has(context, p)) {
                    has = false;
                    break;
                }
            }
            return has;
        }

        public static Map<String, PermissionUtils.PermissionResponse> requestRuntimePermissions(@NotNull Activity activity, int requestCode) {
            return PermissionUtils.requestRuntimePermissions(activity, Arrays.asList(PERMISSIONS), requestCode);
        }
    }
}