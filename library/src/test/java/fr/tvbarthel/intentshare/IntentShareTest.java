package fr.tvbarthel.intentshare;

import android.content.Context;
import android.net.Uri;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;

/**
 * Test for {@link IntentShare}
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class IntentShareTest {

    @Test
    public void testTweetLimitEquals() throws Exception {
        Context context = mock(Context.class);
        String tweetLimitExceeded = "Tweet 140-character limit over exceeded with a "
            + "very very very very very very very very very very very very very"
            + "very ver"
            + "long text of 140 char";
        Assert.assertEquals(140, tweetLimitExceeded.length());
        IntentShare.with(context).twitterBody(tweetLimitExceeded);
    }

    @Test
    public void testImageUriSupported() throws Exception {
        Context context = mock(Context.class);
        Uri image = Uri.parse("content://fr.tvbarthel.test.fileprovider/test.png");
        IntentShare.with(context).image(image);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testImageUriSchemeNotSupported() throws Exception {
        Context context = mock(Context.class);
        Uri image = Uri.parse("http://tvbarthel.com/test.png");
        IntentShare.with(context).image(image);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testImageUriExtensionNotSupported() throws Exception {
        Context context = mock(Context.class);
        Uri image = Uri.parse("content://fr.tvbarthel.test.fileprovider/test.html");
        IntentShare.with(context).image(image);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFacebookUriSchemeNotSupported() {
        Context context = mock(Context.class);
        Uri uri = Uri.parse("mailto://thomas.barthelemy.utc@gmail.com");
        IntentShare.with(context).facebookBody(uri);
    }

    @Test
    public void testFacebookURiSchemeSupported() {
        Context context = mock(Context.class);
        Uri uri = Uri.parse("http://www.tvbarthel.com");
        IntentShare.with(context).facebookBody(uri);
    }


}
