package com.timerx.contact

import co.touchlab.kermit.Logger
import com.timerx.BuildFlags
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class ContactProviderIos : ContactProvider {
    override fun contact() {
        val mailtoString = "mailto:${BuildFlags.supportEmail}?subject=${SUPPORT_SUBJECT}"
        val mailtoUrl = NSURL(string = mailtoString)
        UIApplication.sharedApplication.openURL(url = mailtoUrl, options = emptyMap<Any?, Any?>()) {
            if (!it) {
                Logger.e { "Could not open mailto link: $mailtoString" }
            }
        }
    }
}
