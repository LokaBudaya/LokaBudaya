package com.dev.lokabudaya.utils

import android.content.Context
import com.dev.lokabudaya.R

object FontCertificateUtils {
    fun readRawResource(context: Context, resId: Int): String {
        return context.resources.openRawResource(resId).bufferedReader().use { it.readText() }
    }

    fun getDevCertificate(context: Context): String {
        return readRawResource(context, R.raw.font_cert_dev)
    }

    fun getProdCertificate(context: Context): String {
        return readRawResource(context, R.raw.font_cert_prod)
    }
}