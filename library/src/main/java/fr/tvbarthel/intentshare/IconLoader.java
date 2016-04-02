package fr.tvbarthel.intentshare;

import android.net.Uri;
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
     * @param iconUri   uri of the icon to load.
     * @param imageView image view in which the icon should be loaded.
     */
    void load(Uri iconUri, ImageView imageView);

    /**
     * Called when the icon doesn't need to be loaded anymore.
     * Should cancel any async loading started previously.
     * <p/>
     * See also : {@link IconLoader#load(Uri, ImageView)}
     *
     * @param imageView image view for which the async loading should be canceled.
     */
    void cancel(ImageView imageView);
}
