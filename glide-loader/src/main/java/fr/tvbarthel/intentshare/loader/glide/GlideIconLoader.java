package fr.tvbarthel.intentshare.loader.glide;

import android.net.Uri;
import android.os.Parcel;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import fr.tvbarthel.intentshare.IconLoader;

/**
 * {@link fr.tvbarthel.intentshare.IconLoader} based on {@link com.bumptech.glide.Glide}
 */
public class GlideIconLoader implements IconLoader {

    /**
     * Parcelable
     */
    public static final Creator<GlideIconLoader> CREATOR = new Creator<GlideIconLoader>() {
        @Override
        public GlideIconLoader createFromParcel(Parcel source) {
            return new GlideIconLoader(source);
        }

        @Override
        public GlideIconLoader[] newArray(int size) {
            return new GlideIconLoader[size];
        }
    };

    /**
     * {@link fr.tvbarthel.intentshare.IconLoader} based on {@link com.bumptech.glide.Glide}
     */
    public GlideIconLoader() {
    }

    /**
     * {@link fr.tvbarthel.intentshare.IconLoader} based on {@link com.bumptech.glide.Glide}
     *
     * @param in parcel.
     */
    protected GlideIconLoader(Parcel in) {
    }

    @Override
    public void load(Uri iconUri, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(iconUri)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(imageView);
    }

    @Override
    public void cancel(ImageView imageView) {
        Glide.clear(imageView);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

}
