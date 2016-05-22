package fr.tvbarthel.intentshare;

import android.os.Parcelable;

import java.util.Comparator;

/**
 * ˙Interface which allow to define which comparator will be provided for sorting the
 * target activity inside the {@link TargetChooserActivity}.
 */
public interface TargetActivityComparatorProvider extends Parcelable {

    /**
     * Provide the comparator used to sort {@link TargetActivity} displayed to the user.
     *
     * @return comparator used to sort {@link TargetActivity} displayed to the user.
     */
    Comparator<TargetActivity> provideComparator();
}
