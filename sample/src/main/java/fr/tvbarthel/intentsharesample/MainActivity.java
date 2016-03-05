package fr.tvbarthel.intentsharesample;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import fr.tvbarthel.intentshare.IntentShare;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.activity_sample_share_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentShare.with(MainActivity.this)
                                .text("Default text you would like to share.")
                                .mailBody("Extended text you would like to share in mail body.")
                                .mailSubject("Mail subject.")
                                .image(Uri.parse("content://com.example.test.fileprovider/data/img.png"))
                                .facebookBody(Uri.parse("http://only-link.com"))
                                .twitterBody("Tweet can only have 127 char!")
                                .deliver();
                    }
                }
        );
    }
}