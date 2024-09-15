import SwiftUI
import shared
import FirebaseAnalytics
import GoogleMobileAds

struct ComposeView: UIViewControllerRepresentable {
    let root: RootComponent
    func makeUIViewController(context: Context) -> UIViewController {
        return Main_iosKt.MainViewController(root: root)
    }
        
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    let root: RootComponent
    var body: some View {
        VStack {
            ComposeView(root: root)
            BannerView(adUnitID: "ca-app-pub-2499949091653906/4852400953")
                .frame(width: GADAdSizeBanner.size.width, height: GADAdSizeBanner.size.height)
        }.ignoresSafeArea(edges: .horizontal)
            .ignoresSafeArea(edges: .top)
            .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
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
