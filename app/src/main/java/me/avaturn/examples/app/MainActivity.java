eyJhbGciOiJSUzI1NiIsImtpZCI6ImU2YWMzNTcyNzY3ZGUyNjE0ZmM1MTA4NjMzMDg3YTQ5MjMzMDNkM2IiLCJ0eXAiOiJKV1QifQ.eyJuYW1lIjoiVEdBIEdBTUVSIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hL0FDZzhvY0pLX1hxckw5YnlGWTZqa3M5bUdkbVJ4dlJCSlpyU2ZpOEN5dHp3RDhtamVRPXM5Ni1jIiwiaXNzIjoiaHR0cHM6Ly9zZWN1cmV0b2tlbi5nb29nbGUuY29tL2luM2Qtd2ViLWF2YXRhcnMtcHJvZCIsImF1ZCI6ImluM2Qtd2ViLWF2YXRhcnMtcHJvZCIsImF1dGhfdGltZSI6MTczMDA0NjY4MCwidXNlcl9pZCI6InBQYU16MVVZd3NYS2hFdmtOdlRJVnUyTHVqVjIiLCJzdWIiOiJwUGFNejFVWXdzWEtoRXZrTnZUSVZ1Mkx1alYyIiwiaWF0IjoxNzMwNDMzNTUzLCJleHAiOjE3MzA0MzcxNTMsImVtYWlsIjoiZ3JlYXp5ZmtuZXZpbDc0QGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJmaXJlYmFzZSI6eyJpZGVudGl0aWVzIjp7Imdvb2dsZS5jb20iOlsiMTEyMzQ5NTQyNDgzMzE4MzE4MzU0Il0sImVtYWlsIjpbImdyZWF6eWZrbmV2aWw3NEBnbWFpbC5jb20iXX0sInNpZ25faW5fcHJvdmlkZXIiOiJjdXN0b20ifX0.UL2_RxK8vi1-XU6XmX3IFhmi7SbbTrzkLB7sY59NX8T_2wJSmeH_YnB-rpPMiN3T6pA7xze8pYYk2WPYP2y_6ECpZVaz95r6smKQIwk73AXHtx7tofyF8gFSV9-J4PWrL7NKuIuXnRmsjJ5Jq8sptPbv17masTua8Rvtab3uzAmyqRdmqBnvw0n99-0M7bmoyYLIKTeNn36KOcU63mJyr3_MhR70XnkXBz7HpmgVZMu8pF5GRFuGQLwFSHbmAqX_eKS8sU_pUd5czy1WFa3_-YKDEMPdKXbuRzLzSYB2O24ZPCP48Ab9l0W-RXLy6sAn8u4mqlD7kTsmOuBc8-SL7Q
    package me.avaturn.examples.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.webkit.WebViewCompat;
import androidx.webkit.WebViewFeature;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage(String.format("Camera permission required for scanning"))
                        .show();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView myWebView = findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        // Override user agent to support sign-in with google
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 12) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.5359.128 Mobile Safari/537.36");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, CAMERA_PERMISSION_REQUEST_CODE);
        }

        myWebView.setWebChromeClient(new WebChromeClient() {
             @Override
             public void onPermissionRequest(PermissionRequest request) {
                 request.grant(request.getResources());
             }
        });

        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // Open all redirects in web-view
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // Always enable sign in with redirect in web-view
                view.evaluateJavascript("window.avaturnFirebaseUseSignInWithRedirect = true;", (result) -> {});
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Always enable sign in with redirect in web-view
                view.evaluateJavascript("window.avaturnFirebaseUseSignInWithRedirect = true;", (result) -> {});
            }
        });

        String URL = "https://demo.avaturn.dev/iframe"; // Replace with your project's domain
        Uri uri = Uri.parse(URL);
        String projectDomain = uri.getScheme() + "://" + uri.getHost();


        if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_LISTENER)) {
            WebViewCompat.addWebMessageListener(myWebView, "native_app",
                    Set.of(projectDomain),
                    (view, message, sourceOrigin, isMainFrame, replyProxy) -> {
                        try {
                            JSONObject obj = new JSONObject(Objects.requireNonNull(message.getData()));
                            JSONObject data = obj.getJSONObject("data");

                            if (!obj.getString("eventName").equals("v2.avatar.exported")) {
                                return;
                            }

                            String url_type = data.getString("urlType");
                            String url = data.getString("url");
                            if (url_type.equals("httpURL")) {
                                new AlertDialog.Builder(this)
                                        .setTitle("Received http URL for glb file")
                                        .setMessage(url)
                                        .show();

                            } else {
                                byte[] glb_bytes = Base64.decode(url.substring(url.lastIndexOf(",") + 1), Base64.DEFAULT);
                                new AlertDialog.Builder(this)
                                        .setTitle("Received data URL for glb file")
                                        .setMessage(String.format("Glb file has %.2f Mb size", glb_bytes.length / 1024. / 1024))
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
            );
        }

        myWebView.loadUrl(URL);
    }
}
