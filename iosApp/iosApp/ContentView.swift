import SwiftUI
import shared

struct ContentView: View {
    let timers = Timers().timers()
	var body: some View {
		Text("TimerX")
        List(timers, id: \.self) { t in
            Text(t.name)
        }
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
