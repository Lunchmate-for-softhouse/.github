package com.example.lunchmate.ui.screens

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import com.example.lunchmate.R
import java.io.InputStream
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.X509TrustManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import android.util.Log
import java.io.IOException
import java.security.cert.CertificateFactory

// Function to create OkHttp client with Swish client certificate
fun createSwishOkHttpClient(context: Context): OkHttpClient {
    try {
        // Load the client certificate from res/raw
        val keyStore = KeyStore.getInstance("PKCS12").apply {
            val certInputStream: InputStream = context.resources.openRawResource(R.raw.certificate) // Your client certificate
            load(certInputStream, "123456".toCharArray()) // Your client certificate password
            certInputStream.close()
        }

        // Load CA certificate from res/raw (replace with your actual CA cert filename)
        val caInputStream: InputStream = context.resources.openRawResource(R.raw.swish_ca_certificate) // Your CA certificate
        val caKeyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
            load(null, null) // Create an empty KeyStore
            setCertificateEntry("ca", CertificateFactory.getInstance("X.509").generateCertificate(caInputStream))
        }
        caInputStream.close()

        // Initialize TrustManager with the CA certificate
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(caKeyStore)

        // Initialize KeyManager with the client certificate
        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        keyManagerFactory.init(keyStore, "123456".toCharArray()) // Your client certificate password

        // Create SSLContext with both KeyManager and TrustManager
        val sslContext = SSLContext.getInstance("TLSv1.2").apply {
            init(keyManagerFactory.keyManagers, trustManagerFactory.trustManagers, null)
        }

        val trustManager = trustManagerFactory.trustManagers[0] as X509TrustManager

        // Build the OkHttpClient with SSL settings
        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .build()
    } catch (e: Exception) {
        Log.e("SwishPayment", "Error initializing SSL context: ${e.message}", e)
        throw RuntimeException("Failed to create OkHttpClient with Swish certificate")
    }
}

// Function to make the Swish payment request
fun makeSwishPaymentRequest(context: Context) {
    try {
        Log.d("SwishPaymentRequest", "Creating OkHttpClient")
        val client = createSwishOkHttpClient(context)

        // JSON data for the payment request
        val jsonPayload = """
            {
                "payeeAlias": "1231181189",
                "amount": "100",
                "currency": "SEK",
                "message": "Payment for Order #12345", 
                "callbackUrl": "https://yourcallbackurl.com/paymentcallback" // Your callback URL
            }
        """.trimIndent()

        // Convert JSON to RequestBody
        val requestBody = jsonPayload.toRequestBody("application/json".toMediaType())
        Log.d("SwishPaymentRequest", "Request Body: $jsonPayload")

        // Build the request with the JSON body
        val request = Request.Builder()
            .url("https://mss.cpc.getswish.net/swish-cpcapi/paymentrequests")  // Swish API URL
            .post(requestBody)  // Add the JSON body here
            .build()

        Log.d("SwishPaymentRequest", "Making payment request")

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: Response) {
                Log.d("SwishPaymentRequest", "Response received")
                if (response.isSuccessful) {
                    // Handle success response
                    val responseBody = response.body?.string()
                    Log.i("SwishPayment", "Payment request successful: $responseBody")
                } else {
                    // Handle error response
                    val errorBody = response.body?.string()
                    Log.e("SwishPayment", "Payment request failed with status ${response.code}: $errorBody")
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Handle failure (e.g., network error)
                Log.e("SwishPayment", "Payment request failed: ${e.message}", e)
            }
        })
    } catch (e: Exception) {
        Log.e("SwishPayment", "Error in making payment request: ${e.message}", e)
    }
}
