package com.example.slotdataviewer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * GitHub Pages で公開しているビューアを表示するだけの WebView アプリ。
 *
 * データは端末に同梱せず毎回ネットワークから取得する。ビューア側が
 * 店舗別・月別に分割された JSON を必要なぶんだけ読むので通信量は小さい。
 * 一度読んだものは WebView のキャッシュに残るため、圏外でも直近の表示は開ける。
 */
class MainActivity : ComponentActivity() {
    companion object {
        const val START_URL = "https://makoto-design.github.io/slot-data-viewer/"
    }

    private lateinit var webView: WebView
    private lateinit var refreshLayout: SwipeRefreshLayout

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            // オフライン時はキャッシュから表示する
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            webViewClient = ViewerWebViewClient()
            loadUrl(START_URL)
        }

        refreshLayout = SwipeRefreshLayout(this).apply {
            addView(webView)
            setOnRefreshListener { webView.reload() }
        }
        setContentView(refreshLayout)

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            },
        )
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }

    private inner class ViewerWebViewClient : WebViewClient() {
        /** ビューア以外へのリンクはアプリ内で開かない */
        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest,
        ): Boolean = !request.url.toString().startsWith(START_URL)

        override fun onPageFinished(view: WebView, url: String) {
            refreshLayout.isRefreshing = false
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError,
        ) {
            refreshLayout.isRefreshing = false
            if (!request.isForMainFrame) return
            view.loadData(
                """
                <html><head><meta charset="utf-8">
                <meta name="viewport" content="width=device-width,initial-scale=1">
                </head>
                <body style="font-family:sans-serif;padding:32px;text-align:center;color:#555">
                <h2>接続できませんでした</h2>
                <p>ネットワーク接続を確認して、画面を下に引っぱると再読み込みします。</p>
                </body></html>
                """.trimIndent(),
                "text/html; charset=utf-8",
                null,
            )
        }
    }
}
