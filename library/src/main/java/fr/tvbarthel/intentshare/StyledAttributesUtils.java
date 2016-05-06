package fr.tvbarthel.intentshare;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

/**
 * Util used to retrieved attributes of the current style.
 */
final class StyledAttributesUtils {

    /**
     * non instantiable class.
     */
    private StyledAttributesUtils() {

    }

    /**
     * Retrieve the selectable background of the current style.
     *
     * @param context context used to retrieve the styled attributes.
     * @return selectable item background res id.
     */
    public static int getSelectableItemBackground(Context context) {
        int selectableItemBackgroundResId;
        int[] attrs = new int[]{android.R.attr.selectableItemBackground};
        TypedValue values = new TypedValue();
        TypedArray array = context.obtainStyledAttributes(values.data, attrs);
        selectableItemBackgroundResId = array.getResourceId(0, 0);
        array.recycle();
        return selectableItemBackgroundResId;
    }
}
