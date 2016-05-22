package fr.tvbarthel.intentsharesample;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.Comparator;

import fr.tvbarthel.intentshare.TargetActivity;
import fr.tvbarthel.intentshare.TargetActivityComparatorProvider;

/**
 * Simple custom {@link TargetActivityComparatorProvider} used to illustrate how to provide
 * a different sorting for the target activities displayed to the user.
 * <p/>
 * This example simply displayed some social media apps at first and then keep the original
 * order returned by the system.
 */
class SocialTargetActivityComparatorProvider implements TargetActivityComparatorProvider {

    private ArrayList<String> prioritizedPackageNames;

    /**
     * Parcelable.
     */
    public static final Creator<SocialTargetActivityComparatorProvider> CREATOR
            = new Creator<SocialTargetActivityComparatorProvider>() {
        @Override
        public SocialTargetActivityComparatorProvider createFromParcel(Parcel source) {
            return new SocialTargetActivityComparatorProvider(source);
        }

        @Override
        public SocialTargetActivityComparatorProvider[] newArray(int size) {
            return new SocialTargetActivityComparatorProvider[size];
        }
    };

    /**
     * Simple custom {@link TargetActivityComparatorProvider} used to illustrate how to provide
     * a different sorting for the target activities displayed to the user.
     * <p/>
     * This example simply displayed some social media apps at first and then keep the original
     * order returned by the system.
     */
    public SocialTargetActivityComparatorProvider() {
        prioritizedPackageNames = new ArrayList<>();
        prioritizedPackageNames.add("com.instagram.android");         // instagram
        prioritizedPackageNames.add("com.snapchat.android");          // snapchat
        prioritizedPackageNames.add("com.pinterest");                 // pinterest
        prioritizedPackageNames.add("com.sgiggle.production");        // tango
        prioritizedPackageNames.add("jom.tencent.mm");                // wechat
        prioritizedPackageNames.add("jp.naver.line.android");         // line
        prioritizedPackageNames.add("com.whatsapp");                  // what's app
        prioritizedPackageNames.add("com.google.android.talk");       // hangout
        prioritizedPackageNames.add("com.google.android.apps.plus");  // G+
        prioritizedPackageNames.add("com.twitter.android");           // twitter
        prioritizedPackageNames.add("com.facebook.orca");             // FB
        prioritizedPackageNames.add("com.facebook.katana");           // FB
    }

    /**
     * Simple custom {@link TargetActivityComparatorProvider} used to illustrate how to provide
     * a different sorting for the target activities displayed to the user.
     * <p/>
     * This example simply displayed some social media apps at first and then keep the original
     * order returned by the system.
     *
     * @param in parcel.
     */
    protected SocialTargetActivityComparatorProvider(Parcel in) {
        prioritizedPackageNames = new ArrayList<>();
        in.readStringList(prioritizedPackageNames);
    }


    @Override
    public Comparator<TargetActivity> provideComparator() {
        return new Comparator<TargetActivity>() {
            @Override
            public int compare(TargetActivity lhs, TargetActivity rhs) {
                int lhsPriority = prioritizedPackageNames.indexOf(lhs.getPackageName());
                int rhsPriority = prioritizedPackageNames.indexOf(rhs.getPackageName());
                if (lhsPriority != -1 && rhsPriority != -1) {
                    return lhsPriority - rhsPriority;
                } else if (lhsPriority != -1) {
                    return -1;
                } else if (rhsPriority != -1) {
                    return 1;
                } else {
                    return 0; // keep the original order if not present in the prioritized list.
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
        dest.writeStringList(prioritizedPackageNames);
    }
}
