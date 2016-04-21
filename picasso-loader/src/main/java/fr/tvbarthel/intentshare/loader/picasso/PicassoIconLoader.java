package fr.tvbarthel.intentshare.loader.picasso;

import android.net.Uri;
import android.os.Parcel;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import fr.tvbarthel.intentshare.IconLoader;

/**
 * {@link IconLoader} based on {@link com.squareup.picasso.Picasso}.
 */
public class PicassoIconLoader implements IconLoader {

    /**
     * Parcelable.
     */
    public static final Creator<PicassoIconLoader> CREATOR = new Creator<PicassoIconLoader>() {
        @Override
        public PicassoIconLoader createFromParcel(Parcel source) {
            return new PicassoIconLoader(source);
        }

        @Override
        public PicassoIconLoader[] newArray(int size) {
            return new PicassoIconLoader[size];
        }
    };

    /**
     * {@link IconLoader} based on {@link com.squareup.picasso.Picasso}.
     */
    public PicassoIconLoader() {
    }

    /**
     * {@link IconLoader} based on {@link com.squareup.picasso.Picasso}.
     *
     * @param in parcel.
     */
    protected PicassoIconLoader(Parcel in) {
    }

    @Override
    public void load(Uri iconUri, ImageView imageView) {
        Picasso.with(imageView.getContext())
                .load(iconUri)
                .fit()
                .centerInside()
                .into(imageView);
    }

    @Override
    public void cancel(ImageView imageView) {
        Picasso.with(imageView.getContext()).cancelRequest(imageView);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

}
