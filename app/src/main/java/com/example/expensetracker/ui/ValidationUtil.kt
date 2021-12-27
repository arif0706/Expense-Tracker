package com.example.expensetracker.ui

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.lang.NumberFormatException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class ValidationUtil {


    companion object {

        fun commaSeparateAmount(amount: String): String {

            val formatter = DecimalFormat("#,###")
            return try {
                formatter.format(amount.replace(",","").toLong())
            } catch (e:NumberFormatException){
                "error"
            }

        }
        fun getTodayDate(): String {
            val dateFormat = SimpleDateFormat("ddMMyyyy", Locale.ENGLISH)
            return dateFormat.format(Date())
        }

        fun getFileExtension(context:Context,uri:Uri): String? {
            val cr=context.contentResolver
            val mime=MimeTypeMap.getSingleton()
            return mime.getExtensionFromMimeType(cr.getType(uri))
        }
    }
}
