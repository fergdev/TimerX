import SwiftUI
import FirebaseCrashlytics
import Firebase
import shared

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        FirebaseApp.configure()
        return true
    }
}

@main
struct iOSApp: App {
      @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

    init() {
       let notificationCenter = UNUserNotificationCenter.current()
         notificationCenter.requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
             if let error = error {
                 // Handle the error here.
                 print("Error requesting notifications permission: \(error)")
             }
         }
    }

	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
