package com.falchio.videoview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.github.videoview.R

private const val SYSTEM_VISIBILITY =
    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

/**
 * @see <a href="https://stackoverflow.com/a/56186877/12980819">source</a>
 * */
class MainActivity : AppCompatActivity() {

    lateinit var webView: WebView
    val frameVideo =
//        "<html><body><iframe src=\"https://rutube.ru/play/embed/6f539a34d288f2010e2cd7f7f72ef43f/\" frameborder=\"0\" style=\"position: relative; height: 100%; width: 100%;\" allow=\"clipboard-write; autoplay\" webkitAllowFullScreen mozallowfullscreen allowFullScreen></iframe></body></html>"
        "<iframe src=\"https://rutube.ru/play/embed/6f539a34d288f2010e2cd7f7f72ef43f/\" frameborder=\"0\" style=\"position: relative; height: 100%; width: 100%;\" allow=\"clipboard-write; autoplay\" webkitAllowFullScreen mozallowfullscreen allowFullScreen></iframe>"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webView = findViewById<View>(R.id.video_view) as WebView
        webView.setWebViewClient(WebViewClient())
        webView.setWebChromeClient(MyChrome())
        val webSettings = webView.getSettings()
        webSettings.javaScriptEnabled = true
        webSettings.allowFileAccess = true
        if (savedInstanceState == null) {
//            mWebView.loadUrl("https://www.youtube.com/")
            webView.loadData(frameVideo, "text/html", "utf-8")
        }
    }

    private inner class MyChrome : WebChromeClient() {
        private var customView: View? = null
        private var customViewCallback: CustomViewCallback? = null
        private var originalOrientation = 0
        private var originalSystemUiVisibility = 0
        override fun getDefaultVideoPoster(): Bitmap? {
            return if (customView == null) {
                null
            } else BitmapFactory.decodeResource(applicationContext.resources, 2130837573)
        }

        override fun onHideCustomView() {
            (window.decorView as FrameLayout).removeView(customView)
            customView = null
            window.decorView.systemUiVisibility = originalSystemUiVisibility
            requestedOrientation = originalOrientation
            customViewCallback!!.onCustomViewHidden()
            customViewCallback = null
        }

        override fun onShowCustomView(
            paramView: View,
            paramCustomViewCallback: CustomViewCallback
        ) {
            if (customView != null) {
                onHideCustomView()
                return
            }
            customView = paramView
            originalSystemUiVisibility = window.decorView.systemUiVisibility
            originalOrientation = requestedOrientation
            customViewCallback = paramCustomViewCallback
            (window.decorView as FrameLayout).addView(customView, FrameLayout.LayoutParams(-1, -1))
            window.decorView.systemUiVisibility =
                SYSTEM_VISIBILITY or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webView.restoreState(savedInstanceState)
    }
}