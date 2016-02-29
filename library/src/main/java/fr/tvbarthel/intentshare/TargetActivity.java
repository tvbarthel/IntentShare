package fr.tvbarthel.intentshare;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.io.File;

/**
 * Plain java model for a sharing target activity.
 */
class TargetActivity {

    private final String packageName;
    private final String activityName;
    private CharSequence activityLabel;
    private Uri iconUri;

    /**
     * Plain java model for a sharing target activity.
     *
     * @param context     context used to load data from resolve info.
     * @param resolveInfo {@link ResolveInfo} linked to the target activity.
     */
    public TargetActivity(Context context, ResolveInfo resolveInfo) {
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        packageName = activityInfo.packageName;
        activityName = activityInfo.name;
        this.iconUri = Uri.parse(
                "android.resource://"
                        + resolveInfo.activityInfo.applicationInfo.packageName
                        + File.separator
                        + resolveInfo.activityInfo.applicationInfo.icon
        );
        this.activityLabel = resolveInfo.loadLabel(context.getPackageManager());
    }

    /**
     * Retrieve the package name of the target activity.
     *
     * @return package name.
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Retrive the name of the target activity.
     *
     * @return activity name.
     */
    public String getActivityName() {
        return activityName;
    }

    /**
     * Retrieve the textual label of the Activity.
     *
     * @return textual label of the Activity.
     */
    public CharSequence getActivityLabel() {
        return activityLabel;
    }

    /**
     * Retrieve the Uri used to access to the target application launcher icon.
     *
     * @return uri linking to the application launcher icon.
     */
    public Uri getIconUri() {
        return iconUri;
    }
}
