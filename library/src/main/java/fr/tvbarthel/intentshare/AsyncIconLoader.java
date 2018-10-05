package fr.tvbarthel.intentshare;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Parcel;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Icon loader based on an {@link AsyncTask}.
 * <p/>
 */
class AsyncIconLoader implements IconLoader {

    /**
     * Parcelable.
     */
    public static final Creator<AsyncIconLoader> CREATOR = new Creator<AsyncIconLoader>() {
        @Override
        public AsyncIconLoader createFromParcel(Parcel source) {
            return new AsyncIconLoader(source);
        }

        @Override
        public AsyncIconLoader[] newArray(int size) {
            return new AsyncIconLoader[size];
        }
    };

    private SparseArray<AsyncIconLoaderTask> task;
    private HashMap<String, Drawable> cachedIcons;

    /**
     * Icon loader based on an {@link AsyncTask}
     * <p/>
     *
     * @param in parcel.
     */
    protected AsyncIconLoader(Parcel in) {
        this();
    }

    /**
     * Icon loader based on an {@link AsyncTask}
     * <p/>
     */
    public AsyncIconLoader() {
        task = new SparseArray<>();
        cachedIcons = new HashMap<>();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @Override
    public void load(ResolveInfo resolveInfo, ImageView imageView) {
        Drawable drawable = cachedIcons.get(resolveInfo.activityInfo.packageName);
        if (drawable != null) {
            imageView.setImageDrawable(drawable);
        } else {
            AsyncIconLoaderTask asyncIconLoaderTask
                    = new AsyncIconLoaderTask(resolveInfo, imageView, cachedIcons);
            task.put(imageView.hashCode(), asyncIconLoaderTask);
            asyncIconLoaderTask.execute();
        }
    }

    @Override
    public void cancel(ImageView imageView) {
        int key = imageView.hashCode();
        AsyncIconLoaderTask asyncIconLoaderTask = task.get(key);
        if (asyncIconLoaderTask != null) {
            asyncIconLoaderTask.cancel(true);
            task.remove(key);
        }
    }

    /**
     * {@link AsyncTask} used to load an icon off the ui thread.
     */
    private static final class AsyncIconLoaderTask extends AsyncTask<Void, Void, Drawable> {

        private static final String TAG = AsyncIconLoaderTask.class.getSimpleName();

        private final WeakReference<ImageView> imageTarget;
        private final PackageManager packageManager;
        private final HashMap<String, Drawable> cachedIcons;
        private ResolveInfo resolveInfo;

        /**
         * {@link AsyncTask} used to load an icon off the ui thread.
         *
         * @param resolveInfo the resolve info object of the correspoding resolved activity
         * @param imageView   image view in which the icon should be loaded.
         * @param cachedIcons list of bitmap to which the new decoded one will be added.
         */
        public AsyncIconLoaderTask(ResolveInfo resolveInfo, ImageView imageView,
                                   HashMap<String, Drawable> cachedIcons) {
            packageManager = imageView.getContext().getPackageManager();
            this.resolveInfo = resolveInfo;

            imageTarget = new WeakReference<>(imageView);
            this.cachedIcons = cachedIcons;
        }

        @Override
        protected Drawable doInBackground(Void... params) {

            if (isCancelled()) {
                return null;
            } else {
                return resolveInfo.activityInfo.loadIcon(packageManager);
            }
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            super.onPostExecute(drawable);
            if (drawable != null) {
                if (imageTarget.get() != null) {
                    imageTarget.get().setImageDrawable(drawable);
                }
                cachedIcons.put(resolveInfo.activityInfo.packageName, drawable);
            } else {
                Log.e(TAG, "Failed to load icon from uri : " + resolveInfo.resolvePackageName);
            }
        }
    }
}
