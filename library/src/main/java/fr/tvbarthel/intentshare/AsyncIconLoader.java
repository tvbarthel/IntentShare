package fr.tvbarthel.intentshare;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;

import java.util.List;

/**
 * Icon loader based on an {@link AsyncTask}.
 * <p/>
 * No cashing is performed for decoded {@link Bitmap}.
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


    /**
     * Icon loader based on an {@link AsyncTask}
     * <p/>
     * No cashing is performed for decoded {@link Bitmap}.
     */
    public AsyncIconLoader() {
        task = new SparseArray<>();
    }

    /**
     * Icon loader based on an {@link AsyncTask}
     * <p/>
     * No cashing is performed for decoded {@link Bitmap}.
     *
     * @param in parcel.
     */
    protected AsyncIconLoader(Parcel in) {
        this();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public void load(Uri iconUri, ImageView imageView) {
        AsyncIconLoaderTask asyncIconLoaderTask = new AsyncIconLoaderTask(iconUri, imageView);
        task.put(imageView.hashCode(), asyncIconLoaderTask);
        asyncIconLoaderTask.execute();
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
    private static final class AsyncIconLoaderTask extends AsyncTask<Void, Void, Bitmap> {

        private static final String TAG = AsyncIconLoaderTask.class.getSimpleName();

        private final ImageView imageTarget;
        private final PackageManager packageManager;
        private final String targetPackage;
        private int iconResId;
        private int targetSize;

        /**
         * {@link AsyncTask} used to load an icon off the ui thread.
         *
         * @param uri       uri of the icon to load.
         * @param imageView image view in which the icon should be loaded.
         */
        public AsyncIconLoaderTask(Uri uri, ImageView imageView) {
            packageManager = imageView.getContext().getPackageManager();
            targetPackage = uri.getAuthority();
            iconResId = 0;

            List<String> pathSegments = uri.getPathSegments();
            if (pathSegments.size() != 1) {
                Log.e(TAG, "Can't find the icon res id for : " + uri.toString());
            } else {
                try {
                    iconResId = Integer.parseInt(pathSegments.get(0));
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Can't parse the icon res id : " + pathSegments.get(0));
                }
            }

            imageTarget = imageView;
            targetSize = imageView.getContext().getResources()
                    .getDimensionPixelSize(R.dimen.target_activity_view_icon_size);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Resources resources;
            try {
                resources = packageManager.getResourcesForApplication(targetPackage);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Wrong package name, can't access to the resources : " + targetPackage);
                return null;
            }

            if (isCancelled()) {
                return null;
            }

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            BitmapFactory.decodeResource(resources, iconResId, options);

            if (isCancelled()) {
                return null;
            }

            options.inSampleSize = calculateInSampleSize(options, targetSize, targetSize);
            options.inJustDecodeBounds = false;

            if (isCancelled()) {
                return null;
            } else {
                return BitmapFactory.decodeResource(resources, iconResId, options);
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                imageTarget.setImageBitmap(bitmap);
            }
        }

        private int calculateInSampleSize(
                BitmapFactory.Options options,
                int reqWidth,
                int reqHeight) {
            final int height = options.outHeight;
            final int width = options.outWidth;

            if (reqWidth == 0 || reqHeight == 0) {
                return 1;
            } else {
                int heightRatio = (int) Math.floor((float) height / (float) reqHeight);
                int widthRatio = (int) Math.floor((float) width / (float) reqWidth);
                return Math.min(heightRatio, widthRatio);
            }
        }
    }
}
