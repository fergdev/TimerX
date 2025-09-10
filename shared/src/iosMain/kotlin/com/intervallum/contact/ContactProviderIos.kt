package com.intervallum.contact

import co.touchlab.kermit.Logger
import com.intervallum.BuildFlags
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class ContactProviderIos : ContactProvider {
    override fun contactSupport() {
        val mailtoString = "mailto:${BuildFlags.supportEmail}?subject=${SUPPORT_SUBJECT}"
        val mailtoUrl = NSURL(string = mailtoString)
        UIApplication.sharedApplication.openURL(url = mailtoUrl, options = emptyMap<Any?, Any?>()) {
            if (!it) {
                Logger.e { "Could not open mailto link: $mailtoString" }
            }
        }
    }
}
