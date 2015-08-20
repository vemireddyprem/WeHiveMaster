package uk.co.wehive.hive.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import uk.co.wehive.hive.R;
import uk.co.wehive.hive.utils.AppConstants;

public class WebActivity extends Activity {

    Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        mIntent = getIntent();
        String url = (String) mIntent.getExtras().get("URL");
        WebView webView = (WebView) findViewById(R.id.webView1);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains(AppConstants.TWITTER_CALLBACK_URL)) {
                    Uri uri = Uri.parse(url);
                    String oauthVerifier = uri.getQueryParameter("oauth_verifier");
                    mIntent.putExtra("oauth_verifier", oauthVerifier);
                    setResult(RESULT_OK, mIntent);
                    finish();
                    return true;
                }
                return false;
            }
        });
        webView.loadUrl(url);
    }
}
