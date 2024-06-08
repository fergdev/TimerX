import SwiftUI
import shared

@main
struct iOSApp: App {

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
