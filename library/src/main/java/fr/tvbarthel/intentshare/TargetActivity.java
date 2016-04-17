package fr.tvbarthel.intentshare;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.io.File;
import java.text.Collator;
import java.util.Comparator;

/**
 * Plain java model for a sharing target activity.
 */
class TargetActivity {

    private final int activityLabelResId;
    private final Uri iconUri;
    private final boolean isMail;
    private final long lastSelection;
    private ResolveInfo resolveInfo;
    private CharSequence label;

    /**
     * Plain java model for a sharing target activity.
     *
     * @param context       context used to load target activity label.
     * @param resolveInfo   {@link ResolveInfo} linked to the target activity.
     * @param lastSelection time stamp in milli of  last selection.
     */
    public TargetActivity(Context context, ResolveInfo resolveInfo, long lastSelection) {
        this.lastSelection = lastSelection;
        this.resolveInfo = resolveInfo;

        int icon = resolveInfo.activityInfo.icon;
        if (icon == 0) {
            icon = resolveInfo.activityInfo.applicationInfo.icon;
        }
        this.iconUri = Uri.parse(
                "android.resource://"
                        + resolveInfo.activityInfo.applicationInfo.packageName
                        + File.separator
                        + icon
        );

        this.activityLabelResId = resolveInfo.labelRes;
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

        if (activityLabelResId != that.activityLabelResId) {
            return false;
        }
        if (isMail != that.isMail) {
            return false;
        }
        if (lastSelection != that.lastSelection) {
            return false;
        }
        if (!iconUri.equals(that.iconUri)) {
            return false;
        }
        return resolveInfo.equals(that.resolveInfo);

    }

    @Override
    public int hashCode() {
        int result = activityLabelResId;
        result = 31 * result + iconUri.hashCode();
        result = 31 * result + (isMail ? 1 : 0);
        result = 31 * result + (int) (lastSelection ^ (lastSelection >>> 32));
        result = 31 * result + resolveInfo.hashCode();
        return result;
    }

    /**
     * Retrieve a unique id used to identify target activity.
     *
     * @return unique id used to identify target activity.
     */
    public String getId() {
        return this.resolveInfo.activityInfo.packageName
                + this.resolveInfo.activityInfo.name;
    }

    /**
     * Retrieve the package name of the target activity.
     *
     * @return package name.
     */
    public String getPackageName() {
        return resolveInfo.activityInfo.packageName;
    }

    /**
     * Retrive the name of the target activity.
     *
     * @return activity name.
     */
    public String getActivityName() {
        return resolveInfo.activityInfo.name;
    }

    /**
     * Retrieve the textual label res id of the Activity.
     *
     * @return textual label res id of the Activity.
     */
    public int getActivityLabelResId() {
        return activityLabelResId;
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
     * Retrieve the label of the target activity.
     *
     * @return return target activity label or null if not yet loaded.
     */
    public CharSequence getLabel() {
        return label;
    }

    /**
     * Resolve info linked to the target activity.
     *
     * @return Resolve info linked to the target activity.
     */
    ResolveInfo getResolveInfo() {
        return resolveInfo;
    }

    /**
     * Label of the target activity.
     *
     * @param label label of the target activity.
     */
    void setLabel(CharSequence label) {
        this.label = label;
    }

    /**
     * Comparator used to sort {@link TargetActivity} based on the recency of their previous
     * selection and their name as fallback when they have never been selected.
     * <p/>
     * The ordering imposed by this comparator on a set of {@link TargetActivity}
     * is not consistent with equals since c.compare(e1, e2)==0 has not the same boolean
     * value as e1.equals(e2).
     */
    public static final class RecencyComparator implements Comparator<TargetActivity> {

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
                return 0;
            }
        }
    }
}
