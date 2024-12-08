package com.fatihbilgin.palletmart.kulanicipanel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.replace
import androidx.navigation.fragment.findNavController
import com.fatihbilgin.palletmart.R
import com.fatihbilgin.palletmart.databinding.FragmentPaymentBinding

class PaymentFragment : Fragment() {

    private lateinit var binding: FragmentPaymentBinding
    private lateinit var webView: WebView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentBinding.inflate(inflater, container, false)

        webView = binding.webView

        // WebView ayarlarını yap
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
               val url = request?.url.toString()
                if (url.contains("/cart")){
                    val bundle = Bundle()
                    bundle.putString("orderStatus", "Sipariş Alındı")
                    findNavController().navigate(R.id.action_paymentFragment_to_cart,bundle)
                    return true
                }
                return false
            }
        }

        // WebView'a ödeme sayfasını yükle
        val url = "http://10.0.2.2:3000"  // Express sunucunuzun URL'si
        webView.loadUrl(url)

        return binding.root
    }

}

