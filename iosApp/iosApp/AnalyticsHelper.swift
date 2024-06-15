//
//  AnalyticsHelper.swift
//  iosApp
//
//  Created by Fergus Hewson on 15/6/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import FirebaseAnalytics


@objc public class AnalyticsHelper: NSObject {
    
    @objc public class func logEvent(eventId :String, parameters: Dictionary<String, Any>?) {
        Analytics.logEvent(eventId, parameters: parameters)
    }
    
}


func wowLogger(eventId :String, parameters: Dictionary<String, Any>?) {
    Analytics.logEvent(eventId, parameters: parameters)
}
