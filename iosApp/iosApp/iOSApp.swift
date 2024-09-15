import SwiftUI
import FirebaseCrashlytics
import FirebaseAnalytics
import Firebase
import shared
import GoogleMobileAds

class AppDelegate: NSObject, UIApplicationDelegate {
    let root: RootComponent = DefaultRootComponent(
        componentContext: DefaultComponentContext(
            lifecycle: ApplicationLifecycle()  
        ),
        webHistoryController: nil
    )
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        FirebaseApp.configure()
        TimerXAnalytics_iosKt.firebaseCallback(callback: FirebaseLoggingCallback())
        GADMobileAds.sharedInstance().start(completionHandler: nil)
        return true
    }
}

class FirebaseLoggingCallback: FirebaseIosCallback {
    
    func logEvent(eventId: String, params: String) {
        let dict = splitStringToDictionary(params, ",", ":")
        Analytics.logEvent(eventId, parameters: dict)
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
    

    init() {
        StartKoinKt.startKoin()
    }
	var body: some Scene {
		WindowGroup {
            ContentView(root: delegate.root)
		}
	}
}
