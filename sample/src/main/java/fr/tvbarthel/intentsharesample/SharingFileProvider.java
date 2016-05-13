package fr.tvbarthel.intentsharesample;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

/**
 * A simple {@link FileProvider} that solves a bug with the com.android.mms application.
 * <p/>
 * http://androidxref.com/5.1.1_r6/xref/packages/apps/Mms/src/com/android/mms/ui/UriImage.java#546
 * <p/>
 * <p/>
 * When sharing an images with a content Uri, the com.android.mms application
 * tries to get the orientation of the image from the provider.
 * <p/>
 * This {@link FileProvider} is a very simple example that handle the query of {@link android.provider.MediaStore.Images.ImageColumns#ORIENTATION}
 * projection.
 * <p/>
 * <b>Note:</b> this {@link FileProvider} always returns '0' for the value of the orientation.
 * If your images do not have the same orientation, you should build your own logic.
 */
public class SharingFileProvider extends FileProvider {

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (isMediaStoreOrientationProjection(projection)) {
            return queryMediaStoreOrientation();
        }

        return super.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    /**
     * Check if a projection corresponds to a {@link android.provider.MediaStore.Images.ImageColumns#ORIENTATION} projection.
     *
     * @param projection the projection to check.
     * @return Returns true is the given projection corresponds to {@link android.provider.MediaStore.Images.ImageColumns#ORIENTATION}, false otherwise.
     */
    private boolean isMediaStoreOrientationProjection(String[] projection) {
        return projection != null && projection.length == 1 && MediaStore.Images.ImageColumns.ORIENTATION.equals(projection[0]);
    }

    /**
     * Query the {@link android.provider.MediaStore.Images.ImageColumns#ORIENTATION}
     *
     * @return Returns a {@link Cursor} with {@link android.provider.MediaStore.Images.ImageColumns#ORIENTATION} set to 0.
     */
    private Cursor queryMediaStoreOrientation() {
        String[] cols = new String[]{MediaStore.Images.ImageColumns.ORIENTATION};
        Object[] values = new Object[]{0};

        final MatrixCursor cursor = new MatrixCursor(cols, 1);
        cursor.addRow(values);
        return cursor;
    }
}
