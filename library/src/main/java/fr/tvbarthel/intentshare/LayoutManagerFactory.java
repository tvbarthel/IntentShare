package fr.tvbarthel.intentshare;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Factory used to handle layout manager according to the device android version seamlessly
 */
final class LayoutManagerFactory {

    private static final int MARSHMALLOW_SPAN_COUNT = 4;

    /**
     * Non instantiable class.
     */
    private LayoutManagerFactory() {

    }

    /**
     * Build the layout manager for the {@link TargetActivity} list displayed to the user
     * during the target activity selection.
     *
     * @param context context used to instantiate layout manager.
     * @return layout manager matching the native look and feel linked to the device SDK version.
     */
    public static RecyclerView.LayoutManager buildLayoutManager(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, MARSHMALLOW_SPAN_COUNT);
            gridLayoutManager.setSpanSizeLookup(new MarshmallowSpanSizeLookup());
            return gridLayoutManager;
        } else {
            return new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        }
    }

    /**
     * SpanSizeLookup used to fit the native look and feel provided by
     * {@link android.content.Intent#createChooser(android.content.Intent, CharSequence)}
     */
    private static final class MarshmallowSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

        @Override
        public int getSpanSize(int position) {
            if (position == 0) {
                return MARSHMALLOW_SPAN_COUNT; // header taking the full width;
            } else {
                return 1;                      // target activity taking one unit;
            }
        }
    }
}
