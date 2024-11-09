import SwiftUI
import FirebaseCrashlytics
import FirebaseAnalytics
import Firebase
import shared
import GoogleMobileAds

class AppDelegate: NSObject, UIApplicationDelegate {
    @State private var isBannerVisible: Bool = true
    let root: RootComponent = DefaultRootComponent(
        componentContext: DefaultComponentContext(
            lifecycle: ApplicationLifecycle()  
        ),
        webHistoryController: nil
    )
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        
        StartKoinKt.startKoin()
        FirebaseApp.configure()
        
        // Kotlin
        TimerXAnalytics_iosKt.firebaseCallback(callback: FirebaseLoggingCallback())
        CrashlyticsManager_iosKt.setCrashlyticsCallback(callback: CrashlyticsCallback())
        
        GADMobileAds.sharedInstance().requestConfiguration.testDeviceIdentifiers = [ "16d05d566ddd35ca547efdf2eeb1496c", "092ea52e6e29e1041f1216b949d6ab7d" ]
        Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(true)

        
        Ads_iosKt.setFactory(adFactory :  {() -> UIViewController in
                let ad = BannerAdView()
                    .frame(width: GADAdSizeBanner.size.width, height: GADAdSizeBanner.size.height)
                return UIHostingController(rootView: ad)
            }
        )
        return true
    }
}

class CrashlyticsCallback: CrashlyticsIosCallback {
    
    func setCrashlyticsCollectionEnabled(enabled: Bool) {
        Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(enabled)
    }
}

class FirebaseLoggingCallback: FirebaseIosCallback {
    
    func logEvent(eventId: String, params: String) {
        let dict = splitStringToDictionary(params, ",", ":")
        Analytics.logEvent(eventId, parameters: dict)
    }
    
    func logScreen(screenName:String){
        Analytics.logEvent(AnalyticsEventScreenView, parameters: [AnalyticsParameterScreenName: screenName])
    }
    
    func logError(error:String){
        let error = NSError(domain: "com.timerx", code: 0, userInfo: [
            NSLocalizedDescriptionKey: error,
        ])

        Crashlytics.crashlytics().record(error: error)
    }
     
    func splitStringToDictionary(_ input: String, _ pairDelimiter: Character, _ keyValueDelimiter: Character) -> [String: String] {
        var result = [String: String]()
        
        // Split the input string into key-value pairs
        let pairs = input.split(separator: pairDelimiter)
        
        for pair in pairs {
            // Split each pair into key and value
            let keyValueArray = pair.split(separator: keyValueDelimiter, maxSplits: 1).map { String($0) }
            
            // Ensure there are exactly two components: key and value
            if keyValueArray.count == 2 {
                let key = keyValueArray[0].trimmingCharacters(in: .whitespacesAndNewlines)
                let value = keyValueArray[1].trimmingCharacters(in: .whitespacesAndNewlines)
                result[key] = value
            }
        }
        
        return result
    }
}

@main
struct iOSApp: App {
      @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
	var body: some Scene {
		WindowGroup {
            ContentView(root: delegate.root)
		}
	}
}
