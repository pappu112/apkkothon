package com.joy.apkkothon;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class NewsDetailsActivity extends AppCompatActivity {
    TextView title, date, author;
    String title_str;
    String post_content;
    WebView webView;
    String pub_date;
    Typeface bdfont;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        // ACTION_BAR
        //getSupportActionBar().setTitle("APK কথন");
        getSupportActionBar().hide();

        String pish = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"font/SolaimanLipi.ttf\")}body {font-family: MyFont;font-size: medium;text-align: justify;}</style></head><body>";
        String pas = "</body></html>";

        bdfont = Typeface.createFromAsset(getAssets(), "font/SolaimanLipi.ttf");

        title = (TextView) findViewById(R.id.news_title);
        date = (TextView) findViewById(R.id.pub_date);
        author = (TextView) findViewById(R.id.author);

        date.setTypeface(bdfont);
        date.setText("প্রকাশিতঃ " + getIntent().getStringExtra("date"));
        author.setTypeface(bdfont);
        author.setText("লিখেছেন: " + getIntent().getStringExtra("author"));

        title_str = "<b>" + getIntent().getStringExtra("title") + "</b>";

        pub_date = getIntent().getStringExtra("date");
        title.setTypeface(bdfont);
        title.setText(Html.fromHtml(title_str));

        post_content = pish + getIntent().getStringExtra("content") + pas;


        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
        webView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.setBackgroundColor(Color.parseColor("#ffffff"));
        webView.loadData("<style>img{display: inline;height: auto;max-width: 100%;}</style>" + post_content, "text/html; charset=UTF-8", null);

        mAdView = (AdView) findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        if (!isNetworkAvailable()) { // loading offline
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack())
        {
            webView.goBack();
        }
        else{
            super.onBackPressed();
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("https://apkkothon.net")) {
                webView.loadUrl(url);
                return true;
            }
            else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
                return true;
            }

        }

    }
}
