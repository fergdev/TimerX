package com.intervallum.contact

import com.intervallum.BuildFlags
import kotlinx.browser.window

class ContactProviderWasm : ContactProvider {
    override fun contactSupport() {
        val email = BuildFlags.supportEmail
        val subject = SUPPORT_SUBJECT
        window.location.href = "mailto:$email?subject=$subject"
    }
}
