import SwiftUI
import core

struct HostScreen: View {
    @State var isSplashScreenActive = true
    
    @ObservedObject var currentScope: SwiftDatasource<BaseScope>
    
    var body: some View {
        if isSplashScreenActive {
            SplashScreenView().onAppear {
                DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                    withAnimation(.easeInOut(duration: 0.3)) {
                        self.isSplashScreenActive = false
                    }
                }
            }
        } else {
            NavigationView {
                switch currentScope.value {
                case is LogInScope:
                    if let scopeValue = currentScope.value as? LogInScope {
                        AuthScreen(scope: scopeValue)
                    }
                case is MainScope:
                    if let scopeValue = currentScope.value as? MainScope {
                        MainScreen(scope: scopeValue)
                    }
                case is ForgetPasswordScope:
                    if let scopeValue = currentScope.value as? ForgetPasswordScope {
                        AuthPasswordRestoreScreen(scope: scopeValue)
                    }
                default:
                    Group {}
                }
                
                if let currentScopeValue = currentScope.value, currentScopeValue.isInProgress {
                    VisualEffectView(effect: UIBlurEffect(style: .dark))
                        .edgesIgnoringSafeArea(.all)
                    if #available(iOS 14.0, *) {
                        ProgressView().progressViewStyle(CircularProgressViewStyle(tint: .white))
                    }
                }
            }
        }
    }
    
    init() {
        currentScope = SwiftDatasource(dataSource: navigator.scope)
        
        setUpNavigationBar()
    }
    
    private func setUpNavigationBar() {
        UINavigationBar.appearance().barTintColor = UIColor(hex: 0xD9EDF9)
        
        var titleTextAttributes: [NSAttributedString.Key: Any] = [.foregroundColor: UIColor(hex: 0x003657)]
        
        if let titleFont = UIFont(name: "Barlow-Medium", size: 17) {
            titleTextAttributes[.font] = titleFont
        }
        
        UINavigationBar.appearance().titleTextAttributes = titleTextAttributes
    }
}

struct SplashScreenView: View {
    var body: some View {
        ZStack {
            AppColor.primary.color.edgesIgnoringSafeArea(.all)
            Image("medico_logo")
                .resizable()
                .scaledToFit()
                .frame(width: 150)
        }
    }
}

struct VisualEffectView: UIViewRepresentable {
    var effect: UIVisualEffect?
    func makeUIView(context: UIViewRepresentableContext<Self>) -> UIVisualEffectView { UIVisualEffectView() }
    func updateUIView(_ uiView: UIVisualEffectView, context: UIViewRepresentableContext<Self>) {
        uiView.effect = effect
    }
}
