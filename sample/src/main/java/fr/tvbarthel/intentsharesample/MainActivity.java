package fr.tvbarthel.intentsharesample;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import fr.tvbarthel.intentshare.IconLoader;
import fr.tvbarthel.intentshare.IntentShare;
import fr.tvbarthel.intentshare.IntentShareListener;
import fr.tvbarthel.intentshare.TargetActivityComparatorProvider;
import fr.tvbarthel.intentshare.loader.glide.GlideIconLoader;
import fr.tvbarthel.intentshare.loader.picasso.PicassoIconLoader;

public class MainActivity extends AppCompatActivity implements
        Adapter.Listener, View.OnClickListener, ExtraProviderDialogFragment.Callback {

    /**
     * Log cat.
     */
    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Const used for saving temp image file for sharing purpose.
     */
    private static final int SHARED_IMAGE_QUALITY = 100;
    private static final String SHARED_DIRECTORY = "sharing";
    private static final String SHARED_IMAGE_FILE = "shared_img.png";
    private static final String FILE_PROVIDER_AUTHORITY = "fr.tvbarthel.intentsharesample.fileprovider";

    private IntentShareListener intentShareListener;

    private PicassoIconLoader picassoIconLoader;
    private IconLoader iconLoader;
    private TargetActivityComparatorProvider customComparatorProvider;
    private GlideIconLoader glideIconLoader;

    private String targetPackage;
    private String dialogTitle;
    private String sharedText;
    private ArrayList<ExtraProviderWrapper> extraProviders;
    private Adapter adapter;
    private Uri facebookLink;
    private String tweet;
    private String mailSubject;
    private String mailBody;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        targetPackage = null;
        customComparatorProvider = null;

        intentShareListener = new IntentShareListener() {
            @Override
            public void onCompleted(String packageName) {
                targetPackage = packageName;
            }

            @Override
            public void onCanceled() {
                Toast.makeText(MainActivity.this, "Sharing canceled", Toast.LENGTH_SHORT).show();
            }
        };

        setUpRecyclerView();
        findViewById(R.id.activity_main_share_button).setOnClickListener(this);
        imageUri = getShareableUri(this, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (targetPackage != null) {
            Toast.makeText(MainActivity.this, "Shared with : " + targetPackage, Toast.LENGTH_SHORT).show();
            targetPackage = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_sample_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sample_icon_loader_default:
                iconLoader = null;
                break;
            case R.id.sample_icon_loader_picasso:
                if (picassoIconLoader == null) {
                    picassoIconLoader = new PicassoIconLoader();
                }
                iconLoader = picassoIconLoader;
                break;
            case R.id.sample_icon_loader_glide:
                if (glideIconLoader == null) {
                    glideIconLoader = new GlideIconLoader();
                }
                iconLoader = picassoIconLoader;
                break;
            case R.id.sorting_default:
                customComparatorProvider = null;
                break;
            case R.id.sorting_custom:
                customComparatorProvider = new SocialTargetActivityComparatorProvider();
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        item.setChecked(true);
        return true;
    }

    @Override
    public void onLearMoreRequested() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.article_url)));
        startActivity(intent);
    }

    @Override
    public void onAddExtraProviderRequested() {
        ExtraProviderDialogFragment.newInstance().show(getSupportFragmentManager());
    }

    @Override
    public void onDialogTitleChanged(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    @Override
    public void onSharedTextChanged(String sharedText) {
        this.sharedText = sharedText;
    }

    @Override
    public void onFacebookLinkChanged(String currentFacebookLink) {
        this.facebookLink = Uri.parse(currentFacebookLink);
    }

    @Override
    public void onTweetChanged(String currentTweet) {
        this.tweet = currentTweet;
    }

    @Override
    public void onMailSubjectChanged(String currentMailSubject) {
        this.mailSubject = currentMailSubject;
    }

    @Override
    public void onMailBodyChanged(String currentMailBody) {
        this.mailBody = currentMailBody;
    }

    @Override
    public void onExtraProviderDetailRequested(int wrapperPosition, ExtraProviderWrapper wrapper) {
        ExtraProviderDialogFragment.newInstance(wrapperPosition, wrapper).show(getSupportFragmentManager());
    }

    @Override
    public void onExtraProviderChanged(int position, ExtraProviderWrapper extraProviderWrapper) {
        extraProviders.remove(position);
        extraProviders.add(position, extraProviderWrapper);
        adapter.notifyExtraProviderChanged(position);
    }

    @Override
    public void onExtraProviderCreated(ExtraProviderWrapper extraProviderWrapper) {
        extraProviders.add(extraProviderWrapper);
        adapter.notifyExtraProviderInserted();
    }


    @Override
    public void onClick(View v) {
        IntentShare intentShare = IntentShare.with(MainActivity.this)
                .chooserTitle(dialogTitle)
                .text(sharedText)
                .mailBody(mailBody)
                .mailSubject(mailSubject)
                .image(imageUri)
                .facebookBody(facebookLink)
                .twitterBody(tweet)
                .listener(intentShareListener);
        if (customComparatorProvider != null) {
            intentShare.comparatorProvider(customComparatorProvider);
        }
        if (iconLoader != null) {
            intentShare.iconLoader(iconLoader);
        }
        for (int i = 0; i < extraProviders.size(); i++) {
            intentShare.addExtraProvider(extraProviders.get(i).getExtraProvider());
        }
        intentShare.deliver();
    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_main_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        extraProviders = new ArrayList<>();

        extraProviders.add(buildPocketProvider());
        extraProviders.add(buildKeepProvider());

        adapter = new Adapter(extraProviders);
        adapter.setListener(this);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Pocket only handle links.
     *
     * @return well instantiate provider.
     */
    private ExtraProviderWrapper buildPocketProvider() {
        // pocket only handle link.
        IntentShare.ExtraProvider provider
                = new IntentShare.ExtraProvider("com.ideashower.readitlater.pro")
                .disableImage()
                .disableSubject()
                .overrideText(getString(R.string.article_url));
        return new ExtraProviderWrapper("Pocket", provider);
    }

    /**
     * Keep can have a subject used as not title.
     *
     * @return well instantiate provider.
     */
    private ExtraProviderWrapper buildKeepProvider() {
        // pocket only handle link.
        IntentShare.ExtraProvider provider
                = new IntentShare.ExtraProvider("com.google.android.keep")
                .overrideSubject(getString(R.string.article_title));
        return new ExtraProviderWrapper("Google Keep", provider);
    }

    private Uri getShareableUri(Context context, Bitmap bitmap) {
        // Compress the drawingCache before saving and sharing.
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, SHARED_IMAGE_QUALITY, bytes);

        // Write the compressed bytes to a files
        final File outputDirectory = new File(context.getFilesDir(), SHARED_DIRECTORY);
        if (outputDirectory.isDirectory() || outputDirectory.mkdirs()) {
            final File shareColorFile = new File(outputDirectory, SHARED_IMAGE_FILE);
            try {
                final FileOutputStream fo = new FileOutputStream(shareColorFile);
                fo.write(bytes.toByteArray());
                fo.close();

                // Get the content uri.
                return FileProvider.getUriForFile(context,
                        FILE_PROVIDER_AUTHORITY, shareColorFile);

            } catch (IOException e) {
                Log.e(TAG, "Fail to write bitmap inside the temp file.");
            }
        } else {
            Log.e(TAG, "Fail to create temp file for bitmap sharing.");
        }
        return null;
    }
}
