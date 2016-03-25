package fr.tvbarthel.intentshare;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.io.File;
import java.text.Collator;
import java.util.Comparator;

/**
 * Plain java model for a sharing target activity.
 */
class TargetActivity {

    private final String packageName;
    private final String activityName;
    private final CharSequence activityLabel;
    private final Uri iconUri;
    private final boolean isMail;
    private final long lastSelection;

    /**
     * Plain java model for a sharing target activity.
     *
     * @param context       context used to load data from resolve info.
     * @param resolveInfo   {@link ResolveInfo} linked to the target activity.
     * @param lastSelection time stamp in milli of  last selection.
     */
    public TargetActivity(Context context, ResolveInfo resolveInfo, long lastSelection) {
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        this.packageName = activityInfo.packageName;
        this.activityName = activityInfo.name;
        this.lastSelection = lastSelection;
        this.iconUri = Uri.parse(
                "android.resource://"
                        + resolveInfo.activityInfo.applicationInfo.packageName
                        + File.separator
                        + resolveInfo.activityInfo.applicationInfo.icon
        );
        this.activityLabel = resolveInfo.loadLabel(context.getPackageManager());
        this.isMail = resolveInfo.filter.hasDataType("message/rfc822");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TargetActivity that = (TargetActivity) o;

        if (isMail != that.isMail) {
            return false;
        }
        if (lastSelection != that.lastSelection) {
            return false;
        }
        if (!packageName.equals(that.packageName)) {
            return false;
        }
        if (!activityName.equals(that.activityName)) {
            return false;
        }
        if (!activityLabel.equals(that.activityLabel)) {
            return false;
        }
        return iconUri.equals(that.iconUri);

    }

    @Override
    public int hashCode() {
        int result = packageName.hashCode();
        result = 31 * result + activityName.hashCode();
        result = 31 * result + activityLabel.hashCode();
        result = 31 * result + iconUri.hashCode();
        result = 31 * result + (isMail ? 1 : 0);
        result = 31 * result + (int) (lastSelection ^ (lastSelection >>> 32));
        return result;
    }

    /**
     * Retrieve a unique id used to identify target activity.
     *
     * @return unique id used to identify target activity.
     */
    public String getId() {
        return this.packageName + this.activityName;
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

    /**
     * Used to know if the target activity is a mail client.
     * <p/>
     * Basically, any activity which can handle type 'message/rfc_822'.
     *
     * @return true if the target activity is a mail client, false otherwise.
     */
    public boolean isMailClient() {
        return this.isMail;
    }

    /**
     * Comparator used to sort {@link TargetActivity} based on the recency of their previous
     * selection and their name as fallback when they have never been selected.
     * <p/>
     * The ordering imposed by this comparator on a set of {@link TargetActivity}
     * is not consistent with equals since c.compare(e1, e2)==0 has not the same boolean
     * value as e1.equals(e2).
     */
    public static class RecencyComparator implements Comparator<TargetActivity> {

        private final Collator mCollator = Collator.getInstance();

        /**
         * Comparator used to sort {@link TargetActivity} based on the recency of their previous
         * selection and their name as fallback when they have never been selected.
         * <p/>
         * The ordering imposed by this comparator on a set of {@link TargetActivity}
         * is not consistent with equals since c.compare(e1, e2)==0 has not the same boolean
         * value as e1.equals(e2).
         */
        public RecencyComparator() {
            mCollator.setStrength(Collator.PRIMARY);
        }

        @Override
        public int compare(TargetActivity lhs, TargetActivity rhs) {
            float lhsScore = lhs.lastSelection;
            float rhsScore = rhs.lastSelection;

            if (lhsScore > 0 && rhsScore > 0) {
                return (int) (rhsScore - lhsScore);
            } else if (lhsScore > 0) {
                return -1;
            } else if (rhsScore > 0) {
                return 1;
            } else {
                return mCollator.compare(lhs.activityLabel, rhs.activityLabel);
            }
        }
    }
}
