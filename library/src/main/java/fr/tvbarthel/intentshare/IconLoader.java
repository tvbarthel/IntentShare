package fr.tvbarthel.intentshare;

import android.content.pm.ResolveInfo;
import android.os.Parcelable;
import android.widget.ImageView;

/**
 * Interface which define the contract of a loader used to load
 * {@link TargetActivity} icons.
 */
public interface IconLoader extends Parcelable {

    /**
     * Called when the icon should be load into the imageView.
     * <p/>
     * See also : {@link IconLoader#cancel(ImageView)}
     *
     * @param resolveInfo the resolve info associated with the corresponding target activity
     * @param imageView image view in which the icon should be loaded.
     */
    void load(ResolveInfo resolveInfo, ImageView imageView);

    /**
     * Called when the icon doesn't need to be loaded anymore.
     * Should cancel any async loading started previously.
     * <p/>
     * See also : {@link IconLoader#load(android.content.pm.ResolveInfo, ImageView)}
     *
     * @param imageView image view for which the async loading should be canceled.
     */
    void cancel(ImageView imageView);
}
