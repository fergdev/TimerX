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
    @State private var isBannerVisible: Bool = true
    var body: some View {
        ComposeView(root: root)
        .ignoresSafeArea(edges: .all)
            .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}

struct BannerAdView: UIViewRepresentable {

    var adUnitID = "ca-app-pub-2499949091653906/4852400953"

    func makeUIView(context: Context) -> GADBannerView {
        let bannerView = GADBannerView(adSize: GADAdSizeBanner)
        bannerView.adUnitID = adUnitID
        bannerView.rootViewController = UIApplication.shared.windows.first?.rootViewController
        bannerView.delegate = context.coordinator
        bannerView.load(GADRequest())
        return bannerView
    }

    func updateUIView(_ uiView: GADBannerView, context: Context) {}

    // Coordinator to handle ad events
    func makeCoordinator() -> Coordinator {
        return Coordinator(self)
    }

    class Coordinator: NSObject, GADBannerViewDelegate {
        var parent: BannerAdView

        init(_ parent: BannerAdView) {
            self.parent = parent
        }

        // Called when ad is successfully loaded
        func bannerViewDidReceiveAd(_ bannerView: GADBannerView) {
            print("Ad loaded")
        }

        // Called when ad fails to load
        func bannerView(_ bannerView: GADBannerView, didFailToReceiveAdWithError error: Error) {
            print("Ad failed to load: \(error.localizedDescription)")
        }
        
        func bannerViewDidRecordImpression(_ bannerView: GADBannerView){
            print("Ad did record impression")
        }
        
        func bannerViewDidRecordClick(_ bannerView: GADBannerView){
            print("Ad did record click")
        }
    }
}
