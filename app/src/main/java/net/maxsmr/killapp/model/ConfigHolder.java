package net.maxsmr.killapp.model;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.maxsmr.commonutils.data.CompareUtils;
import net.maxsmr.commonutils.data.FileHelper;
import net.maxsmr.commonutils.data.gson.GsonHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public final class ConfigHolder {

    @NotNull
    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setLenient().create();

    @NotNull
    private final Context context;

    @Nullable
    private Uri path;

    @Nullable
    private Config config;

    public ConfigHolder(@NotNull Context context, @Nullable Uri path) {
        this.context = context;
        setPath(path);
    }

    @Nullable
    public Uri getPath() {
        return path;
    }

    public void setPath(@Nullable Uri path) {
        if (!CompareUtils.objectsEqual(this.path, path)) {
            this.path = path;
            updateConfig();
        }
    }

    @Nullable
    public Config getConfig() {
        return config;
    }

    public void updateConfig() {
        if (path == null) {
            throw new IllegalArgumentException("Config uri is not specified");
        }
        final String stringPath = path.toString();
        final String scheme = path.getScheme();
        String resourcePath = null;
        if (!TextUtils.isEmpty(scheme)) {
            int pathIndex = scheme.length() + 3;
            if (pathIndex < stringPath.length() - 1) {
                resourcePath = stringPath.substring(pathIndex);
            }
        }
        if (TextUtils.isEmpty(resourcePath)) {
            resourcePath = stringPath;
        }
        if (TextUtils.isEmpty(resourcePath)) {
            throw new IllegalArgumentException("Config resource path is not specified");
        }
        String content = null;
        if (CompareUtils.stringsEqual(scheme, "asset_file", false)) {
            content = FileHelper.readStringFromAsset(context, resourcePath);
        } else if (TextUtils.isEmpty(scheme) || CompareUtils.stringsEqual(scheme, ContentResolver.SCHEME_FILE, false)) {
            content = FileHelper.readStringFromFile(new File(resourcePath));
        } else {
            throw new IllegalArgumentException("Unknown uri scheme: " + scheme);
        }
        config = GsonHelper.fromJsonObjectString(gson, content, Config.class);
    }



}
