package com.timerx.contact

import co.touchlab.kermit.Logger
import com.timerx.BuildFlags
import java.awt.Desktop
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class ContactProviderDesktop : ContactProvider {
    override fun contact() {
        val desktop = Desktop.getDesktop()
        if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.MAIL)) {
            val mailto =
                "mailto:${BuildFlags.supportEmail}?subject=${SUPPORT_SUBJECT.encode()}"
            desktop.mail(URI.create(mailto))
        } else {
            Logger.e {
                "Could not send email" +
                    " isDesktopSupported='${Desktop.isDesktopSupported()}' " +
                    " isMailSupported='${desktop.isSupported(Desktop.Action.MAIL)}"
            }
        }
    }

    private fun String.encode() = URLEncoder.encode(this, StandardCharsets.UTF_8.toString())
        .replace("+", "%20")
}
