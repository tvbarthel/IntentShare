package fr.tvbarthel.intentshare;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manager used to handle all logic linked to {@link TargetActivity}
 */
class TargetActivityManager {

    /**
     * Shared preferences key used to store chosen activities.
     */
    private static final String SHARED_PREF_KEY = "shared_pref_target_activities";

    /**
     * Pattern for last selection key.
     * string 1 : package name
     * string 2 : activity name
     */
    private static final String KEY_LAST_SELECTION = "shared_pref_last_selection_$1%s_$2%s";

    /**
     * List of target activities.
     */
    private List<TargetActivity> targetActivities;

    /**
     * Shared preferences used to store target activities scores.
     */
    private SharedPreferences sharedPreferences;

    /**
     * Manager used to handle all logic linked to {@link TargetActivity}
     */
    public TargetActivityManager() {
        targetActivities = new ArrayList<>();
    }

    /**
     * Resolve the list of {@link android.app.Activity} which can be targeted for sharing content.
     * <p/>
     * Basically, resolve the list of {@link android.app.Activity} which can handled
     * {@link Intent#ACTION_SEND}.
     *
     * @param context context used to resolves target activities.
     */
    public void resolveTargetActivities(Context context) {
        targetActivities.clear();

        sharedPreferences = context.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);

        PackageManager packageManager = context.getPackageManager();

        Intent intentShare = new Intent(Intent.ACTION_SEND);
        intentShare.putExtra(Intent.EXTRA_TEXT, "queryText");
        intentShare.setType("text/plain");
        List<ResolveInfo> shareActivities = packageManager.queryIntentActivities(
                intentShare,
                PackageManager.GET_RESOLVED_FILTER
        );

        for (int i = 0; i < shareActivities.size(); i++) {
            ResolveInfo targetActivityInfo = shareActivities.get(i);
            IntentFilter filter = targetActivityInfo.filter;
            if (filter.hasDataType("text/plain")) {

                String lastSelectionKey = getLastSelectionKey(
                        targetActivityInfo.activityInfo.packageName,
                        targetActivityInfo.activityInfo.name
                );

                long lastSelection = sharedPreferences.getLong(lastSelectionKey, 0);

                targetActivities.add(
                        new TargetActivity(
                                context,
                                targetActivityInfo,
                                lastSelection
                        )
                );
            }
        }

        Collections.sort(targetActivities, new TargetActivity.RecencyComparator());
    }

    private String getLastSelectionKey(String packageName, String activityName) {
        return String.format(KEY_LAST_SELECTION, packageName, activityName);
    }

    /**
     * Retrieve the list of target activities for sharing content.
     * <p/>
     * See also : {@link TargetActivityManager#resolveTargetActivities(Context)}
     *
     * @return list of target activities. Empty list if target activities aren't resolved.
     */
    public List<TargetActivity> getTargetActivities() {
        return targetActivities;
    }

    /**
     * Start a target activity with well field params according to the given {@link IntentShare}
     *
     * @param context        context used to start the activity.
     * @param targetActivity target activity to start.
     * @param intentShare    data which should be send to the target activity.
     */
    public void startTargetActivity(Context context, TargetActivity targetActivity, IntentShare intentShare) {
        context.startActivity(
                buildTargetActivityIntent(
                        targetActivity,
                        intentShare
                )
        );

        sharedPreferences
                .edit()
                .putLong(getLastSelectionKey(
                                targetActivity.getPackageName(),
                                targetActivity.getActivityName()
                        ),
                        System.currentTimeMillis()
                )
                .apply();

    }

    /**
     * Build the intent with {@link Intent#ACTION_SEND} action used to start the target activity
     * with well field params according to the given {@link IntentShare}.
     *
     * @param targetActivity target activity which should be start.
     * @param intentShare    shared content used to field the intent params.
     * @return {@link Intent#ACTION_SEND} field to start the target activity.
     */
    private Intent buildTargetActivityIntent(TargetActivity targetActivity, IntentShare intentShare) {
        String packageName = targetActivity.getPackageName();
        String activityName = targetActivity.getActivityName();
        ComponentName componentName = new ComponentName(
                packageName,
                activityName
        );
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        if (targetActivity.isMailClient()) { // mail target
            intent.putExtra(Intent.EXTRA_TEXT, intentShare.mailBody);
            intent.putExtra(Intent.EXTRA_SUBJECT, intentShare.mailSubject);
            addImageExtras(intent, intentShare.imageUri);
        } else { // other target
            intent.putExtra(Intent.EXTRA_TEXT, intentShare.text);
            addImageExtras(intent, intentShare.imageUri);
        }
        applyExtraProvider(intent, packageName, intentShare.extraProviders);
        intent.setComponent(componentName);
        return intent;
    }

    /**
     * Apply the extra provider to the current intent if one is associated to the targeted package.
     *
     * @param intent            intent send.
     * @param targetPackageName targeted package.
     * @param extraProviders    list of extra providers.
     */
    private void applyExtraProvider(
            Intent intent,
            String targetPackageName,
            ArrayList<IntentShare.ExtraProvider> extraProviders) {

        IntentShare.ExtraProvider extraProvider = null;
        for (int i = 0; i < extraProviders.size(); i++) {
            IntentShare.ExtraProvider provider = extraProviders.get(i);
            if (provider.packageName.equals(targetPackageName)) {
                extraProvider = provider;
                break;
            }
        }

        if (extraProvider != null) {
            if (extraProvider.textDisabled) {
                intent.removeExtra(Intent.EXTRA_TEXT);
            } else if (extraProvider.overriddenText != null) {
                intent.putExtra(Intent.EXTRA_TEXT, extraProvider.overriddenText);
            }
            if (extraProvider.subjectDisabled) {
                intent.removeExtra(Intent.EXTRA_SUBJECT);
            } else if (extraProvider.overriddenSubject != null) {
                intent.putExtra(Intent.EXTRA_SUBJECT, extraProvider.overriddenSubject);
            }
            if (extraProvider.imageDisabled) {
                intent.removeExtra(Intent.EXTRA_STREAM);
            } else if (extraProvider.overriddenImage != null) {
                intent.putExtra(Intent.EXTRA_STREAM, extraProvider.overriddenImage);
            }
        }
    }

    private void addImageExtras(Intent intent, Uri imageUri) {
        if (imageUri != null) {
            intent.putExtra(Intent.EXTRA_STREAM, imageUri);
            intent.setType("image/jpeg");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

}
