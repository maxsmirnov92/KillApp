package net.maxsmr.killapp.ui;

import android.app.Activity;

import net.maxsmr.killapp.app.UsedPermissions;

public class PermissionsActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        UsedPermissions.P.requestRuntimePermissions(this, 0); // I don't fucking care
        finish();
    }
}
