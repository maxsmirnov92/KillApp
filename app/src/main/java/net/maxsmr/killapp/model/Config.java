package net.maxsmr.killapp.model;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Config {

    @SerializedName("restriction_packages")
    @Nullable
    private List<String> restrictionPackages;

    @SerializedName("include_system_packages")
    private boolean includeSystemPackages;

    @SerializedName("kill_delay")
    private long killDelay;

    public boolean hasAppsToKill() {
        return !getRestrictionPackages().isEmpty();
    }

    @NotNull
    public List<String> getRestrictionPackages() {
        if (restrictionPackages == null) {
            restrictionPackages = new ArrayList<>();
        }
        return new ArrayList<>(restrictionPackages);
    }

    public boolean isIncludeSystemPackages() {
        return includeSystemPackages;
    }

    public long getKillDelay() {
        return killDelay;
    }

    @NotNull
    @Override
    public String toString() {
        return "Config{" +
                "restrictionPackages=" + restrictionPackages +
                ", includeSystemPackages=" + includeSystemPackages +
                ", killDelay=" + killDelay +
                '}';
    }
}
