package com.moare.android.ui.view

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

// NOTE: WebView로 열었을때 노션이 잘 동작하지 않아 CustomTabsIntent를 사용함.

@Composable
fun OpenUrlWithCustomTab(url: String) {
    val context = LocalContext.current

    val intent = CustomTabsIntent.Builder()
        .setShowTitle(true)
        .build()

    intent.launchUrl(context, Uri.parse(url))
}