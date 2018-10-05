package fr.tvbarthel.intentshare;

import android.content.pm.ResolveInfo;
import android.os.Parcel;

import java.util.Comparator;

/**
 * Plain java model for a sharing target activity.
 */
public class TargetActivity {

    private final int activityLabelResId;
    private final boolean isMail;
    private final long lastSelection;
    private ResolveInfo resolveInfo;
    private CharSequence label;

    /**
     * Plain java model for a sharing target activity.
     *
     * @param resolveInfo {@link ResolveInfo} linked to the target activity.
     * @param lastSelection time stamp in milli of  last selection.
     */
    public TargetActivity(ResolveInfo resolveInfo, long lastSelection) {
        this.lastSelection = lastSelection;
        this.resolveInfo = resolveInfo;

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
        return resolveInfo.equals(that.resolveInfo);

    }

    @Override
    public int hashCode() {
        int result = activityLabelResId;
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
     * Return a timestamp of the last selection inside the sharing dialog from you application.
     *
     * @return timestamp of the last selection in milliseconds since January 1, 1970 00:00:00.0 UTC
     * or 0 if the target activity has never been selected by the user.
     */
    public long getLastSelection() {
        return lastSelection;
    }

    /**
     * Retrieve the label of the target activity.
     *
     * @return return target activity label or null if not yet loaded.
     */
    CharSequence getLabel() {
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
     * selection and their default order as fallback when they have never been selected.
     * <p/>
     * The ordering imposed by this comparator on a set of {@link TargetActivity}
     * is not consistent with equals since c.compare(e1, e2)==0 has not the same boolean
     * value as e1.equals(e2).
     */
    public static final class RecencyComparatorProvider implements TargetActivityComparatorProvider {

        /**
         * Parcelable.
         */
        public static final Creator<RecencyComparatorProvider> CREATOR = new Creator<RecencyComparatorProvider>() {

            @Override
            public RecencyComparatorProvider createFromParcel(Parcel source) {
                return new RecencyComparatorProvider(source);
            }

            @Override
            public RecencyComparatorProvider[] newArray(int size) {
                return new RecencyComparatorProvider[size];
            }
        };

        /**
         * Comparator used to sort {@link TargetActivity} based on the recency of their previous
         * selection and their default order as fallback when they have never been selected.
         * <p/>
         * The ordering imposed by this comparator on a set of {@link TargetActivity}
         * is not consistent with equals since c.compare(e1, e2)==0 has not the same boolean
         * value as e1.equals(e2).
         */
        public RecencyComparatorProvider() {

        }

        /**
         * Comparator used to sort {@link TargetActivity} based on the recency of their previous
         * selection and their default order as fallback when they have never been selected.
         * <p/>
         * The ordering imposed by this comparator on a set of {@link TargetActivity}
         * is not consistent with equals since c.compare(e1, e2)==0 has not the same boolean
         * value as e1.equals(e2).
         *
         * @param in parcel.
         */
        protected RecencyComparatorProvider(Parcel in) {

        }

        @Override
        public Comparator<TargetActivity> provideComparator() {
            return new Comparator<TargetActivity>() {
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
            };
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

        }
    }
}
