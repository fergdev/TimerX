package com.timerx.contact

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import com.timerx.BuildFlags

private const val MAILTO = "mailto"
private const val SEND_EMAIL = "Send Email"

class ContactProviderAndroid(
    private val context: ComponentActivity
) : ContactProvider {
    override fun contact() {
        val emailAddress = arrayOf(BuildFlags.supportEmail)
        val intent = Intent(
            Intent.ACTION_SENDTO,
            Uri.fromParts(MAILTO, BuildFlags.supportEmail, null)
        )
        intent.putExtra(Intent.EXTRA_EMAIL, emailAddress)
        intent.putExtra(Intent.EXTRA_SUBJECT, SUPPORT_SUBJECT)
        context.startActivity(Intent.createChooser(intent, SEND_EMAIL))
    }
}
