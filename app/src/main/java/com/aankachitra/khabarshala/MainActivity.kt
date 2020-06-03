package com.aankachitra.khabarshala

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity(){
    var myWebView: WebView? = null
    var progressBar: ProgressBar? = null
    var banner: RelativeLayout? = null
    lateinit var mySwipeRefreshLayout: SwipeRefreshLayout

    var doubleBackToExitOnce: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!isConnected()) {
            startDialog("You are not connected to internet!")
        } else {
            loadSite()
        }
        mySwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        mySwipeRefreshLayout.setOnRefreshListener { myWebView!!.reload()
            if (null != mySwipeRefreshLayout) {
                mySwipeRefreshLayout.isRefreshing = false;
            }}
    }

    private fun loadSite() {
        myWebView = findViewById(R.id.webview)
        progressBar = findViewById(R.id.progress_Bar)
        banner = findViewById(R.id.rl_home_banner)
        val webSettings: WebSettings = myWebView!!.settings
        webSettings.javaScriptEnabled = true
        webSettings.setSupportMultipleWindows(true)
        webSettings.allowFileAccess = true

        myWebView!!.overScrollMode = View.OVER_SCROLL_NEVER
        myWebView!!.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                setValue(newProgress)
                super.onProgressChanged(view, newProgress)
            }
        }

        myWebView!!.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar!!.visibility = View.INVISIBLE
                progressBar!!.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                progressBar!!.visibility = View.VISIBLE
                progressBar!!.visibility = View.INVISIBLE
            }
        }



        webSettings.domStorageEnabled = true
        webSettings.allowFileAccessFromFileURLs = true
        webSettings.loadsImagesAutomatically = true
        myWebView!!.loadUrl("https://khabarshala.com")

        Handler().postDelayed({
            kotlin.run { banner!!.visibility = View.INVISIBLE }
        }, 2000)
    }

    fun setValue(progress: Int) {
        progressBar!!.progress = progress
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView!!.canGoBack()) {
            myWebView!!.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onStop() {
        super.onStop()
    }
    override fun onBackPressed() {
        if (doubleBackToExitOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitOnce = true
        //displays a toast message when user clicks exit button
        Toast.makeText(applicationContext, "please press again to exit ", Toast.LENGTH_SHORT).show()

        //displays the toast message for a while
        Handler().postDelayed({
            kotlin.run { doubleBackToExitOnce = false }
        }, 2000)

    }

    private fun isConnected(): Boolean {
        var connected = false
        try {
            val cm =
                applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val nInfo = cm.activeNetworkInfo
            connected = nInfo != null && nInfo.isAvailable && nInfo.isConnected
            return connected
        } catch (e: Exception) {
            Log.e("Connectivity Exception", e.message)
        }
        return connected
    }

    private fun startDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Oops!")
        //set message for alert dialog
        builder.setMessage(message)

        //performing positive action
        builder.setPositiveButton("Ok") { dialogInterface, which ->
            finish();
            exitProcess(0);
        }

        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}

