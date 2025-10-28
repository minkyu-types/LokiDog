import SwiftUI
import ComposeApp

@main
struct ComposeApp: App {

    init() {
        KoinHelperKt.doInitKoin()

        // iOS 알람 델리게이트 등록
        let alarmReceiver = AlarmReceiver()
        alarmReceiver.register()
    }

    var body: some Scene {
        WindowGroup {
            ContentView().ignoresSafeArea(.all)
        }
    }
}

struct ContentView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return MainKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // Updates will be handled by Compose
    }
}
