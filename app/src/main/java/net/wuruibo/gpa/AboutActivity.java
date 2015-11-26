package net.wuruibo.gpa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.umeng.analytics.MobclickAgent;

import net.wuruibo.gpa.utils.VersionUtil;

/**
 * Created by Robert on 2015/9/21.
 */
public class AboutActivity extends Activity
{
    private WebView webView;
    private RelativeLayout about_back;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        about_back = (RelativeLayout)findViewById(R.id.about_back);
        about_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/about.html");
        WebViewClient webClient = new WebViewClient()
        {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                webView.loadUrl("javascript: insertVersion('"+ VersionUtil.getVersionName(AboutActivity.this)+"')");
                super.onPageFinished(view, url);
            }
        };
        webView.setWebViewClient(webClient);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
