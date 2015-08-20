package uk.co.wehive.hive.utils.twitter;

import java.io.File;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import uk.co.wehive.hive.utils.AppConstants;

public class TwitterHelper {

    private Twitter mTwitter;
    private RequestToken mRequestToken = null;
    private String mAccessToken;
    private String mAccessTokenSecret;


    public TwitterHelper() {
        mTwitter = new TwitterFactory().getInstance();
        mRequestToken = null;

        mTwitter.setOAuthConsumer(AppConstants.TWITTER_CONSUMER_KEY, AppConstants.TWITTER_CONSUMER_SECRET);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mRequestToken = mTwitter.getOAuthRequestToken(AppConstants.TWITTER_CALLBACK_URL);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void storeOAuthVerifier(final String OAuth) {

        AccessToken at;
        try {
            at = mTwitter.getOAuthAccessToken(mRequestToken, OAuth);
            mAccessToken = at.getToken();
            mAccessTokenSecret = at.getTokenSecret();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    public String getAuthenticationURL() {
        return mRequestToken.getAuthenticationURL();
    }

    public boolean verifyLoginData() {
        return mAccessToken != null && mAccessToken.length() > 0 && mAccessTokenSecret != null && mAccessTokenSecret.length() > 0;
    }

    public boolean sendTweet(String tweetText, File file) {
        if ((mAccessToken != null) && (mAccessTokenSecret != null)) {
            Configuration conf = new ConfigurationBuilder()
                    .setOAuthConsumerKey(AppConstants.TWITTER_CONSUMER_KEY)
                    .setOAuthConsumerSecret(AppConstants.TWITTER_CONSUMER_SECRET)
                    .setOAuthAccessToken(mAccessToken)
                    .setOAuthAccessTokenSecret(mAccessTokenSecret)
                    .build();

            Twitter twitter = new TwitterFactory(conf).getInstance();

            try {
                StatusUpdate status = new StatusUpdate(tweetText);
                if (file != null) {
                    status.setMedia(file);
                }
                twitter.updateStatus(status);
            } catch (TwitterException e) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public String getAccessTokenSecret() {
        return mAccessTokenSecret;
    }

    public void setAccessTokenSecret(String mAccessTokenSecret) {
        this.mAccessTokenSecret = mAccessTokenSecret;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String mAccessToken) {
        this.mAccessToken = mAccessToken;
    }
}
