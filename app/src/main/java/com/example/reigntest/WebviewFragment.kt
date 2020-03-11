package com.example.reigntest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment

class WebviewFragment : Fragment() {
    lateinit var url:String
    lateinit var  web_view:WebView
    companion object {
        @JvmStatic
        fun newInstance(url: String) = WebviewFragment().apply {
            arguments = Bundle().apply {
                putString("url", url)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString("url")?.let {
            url = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_webview, container, false)
        web_view = view.findViewById(R.id.webview)
        initTemplate(url)
        return view
    }

    fun initTemplate(url:String){
        web_view.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        web_view.loadUrl(url)
    }
}