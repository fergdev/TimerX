package com.intervallum.contact

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.intervallum.BuildFlags

private const val MAILTO = "mailto"
private const val SEND_EMAIL = "Send Email"

class ContactProviderAndroid(
    private val context: Context
) : ContactProvider {
    override fun contactSupport() {
        val emailAddress = arrayOf(BuildFlags.supportEmail)
        val intent = Intent(
            Intent.ACTION_SENDTO,
            Uri.fromParts(MAILTO, BuildFlags.supportEmail, null)
        )
        intent.putExtra(Intent.EXTRA_EMAIL, emailAddress)
        intent.putExtra(Intent.EXTRA_SUBJECT, SUPPORT_SUBJECT)
        val chooserIntent = Intent.createChooser(intent, SEND_EMAIL)
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }
}
