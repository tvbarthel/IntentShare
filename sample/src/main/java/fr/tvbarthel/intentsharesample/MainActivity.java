package fr.tvbarthel.intentsharesample;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import fr.tvbarthel.intentshare.IntentShare;
import fr.tvbarthel.intentshare.IntentShareListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private IntentShareListener intentShareListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intentShareListener = new IntentShareListener() {
            @Override
            public void onCompleted(String packageName) {
                Log.d(TAG, "onCompleted : " + packageName);
            }

            @Override
            public void onCanceled() {
                Log.d(TAG, "onCanceled");
            }
        };

        findViewById(R.id.activity_sample_share_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        IntentShare.with(MainActivity.this)
                .text("Default text you would like to share.")
                .mailBody("Extended text you would like to share in mail body.")
                .mailSubject("Mail subject.")
                .image(Uri.parse("content://com.example.test.fileprovider/data/img.png"))
                .facebookBody(Uri.parse("http://only-link.com"))
                .twitterBody("Tweet can only have 127 char!")
                .listener(intentShareListener)
                .deliver();
    }
}
