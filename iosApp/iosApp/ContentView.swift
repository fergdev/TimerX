import SwiftUI
import shared
import FirebaseAnalytics
import GoogleMobileAds


struct ComposeView: UIViewControllerRepresentable {
    var bannerView: GADBannerView!
    
    func makeUIViewController(context: Context) -> UIViewController {
        return Main_iosKt.MainViewController()
    }
        
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}


struct ContentView: View {
    var body: some View {
        VStack {
            ComposeView()
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
            BannerView(adUnitID: "ca-app-pub-2499949091653906/4852400953")
                .frame(width: 320, height: 50, alignment: .center)
        }
    }
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}

struct BannerView: UIViewRepresentable {
    var adUnitID: String

    func makeUIView(context: Context) -> GADBannerView {
        let banner = GADBannerView(adSize: GADAdSizeBanner)
        banner.adUnitID = adUnitID
        banner.rootViewController = UIApplication.shared.windows.first?.rootViewController
        banner.load(GADRequest())
        return banner
    }

    func updateUIView(_ uiView: GADBannerView, context: Context) {
        // No update code needed
    }
}
