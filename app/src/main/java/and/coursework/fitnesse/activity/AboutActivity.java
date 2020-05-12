package and.coursework.fitnesse.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import and.coursework.fitnesse.R;

public class AboutActivity extends AppCompatActivity {

    /*Enables webview to show the HTML user guide*/
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        WebView wv = findViewById(R.id.webview);
        wv.setWebViewClient(new WebViewClient());
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadUrl(this.getResources().getString(R.string.WEBVIEW_LOAD_URL));
    }
}
