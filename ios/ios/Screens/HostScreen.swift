import SwiftUI
import core

struct HostScreen: View {
    @State var isSplashScreenActive = true
    
    @ObservedObject var currentScope: SwiftDatasource<BaseScope>
    
    var body: some View {
        if isSplashScreenActive {
            self.splashScreen
                .onAppear {
                    DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                        withAnimation(.easeInOut(duration: 0.3)) {
                            self.isSplashScreenActive = false
                        }
                    }
                }
        } else {
            ZStack {
                NavigationView {
                    self.currentView
                }
            
                if let currentScopeValue = currentScope.value, currentScopeValue.isInProgress {
                    ActivityView()
                }
            }
        }
    }
    
    var currentView: some View {
        Group {
            switch currentScope.value {
            
            case let scopeValue as LogInScope:
                AuthScreen(scope: scopeValue)
                
            case let scopeValue as MainScope:
                MainScreen(scope: scopeValue)
                
            case let scopeValue as ForgetPasswordScope:
                AuthPasswordRestoreScreen(scope: scopeValue)
                
            case let scopeValue as SignUpScope:
                SignUpScreen(scope: scopeValue)
                
            default:
                Group {}
            }
        }
    }
    
    var splashScreen: some View {
        ZStack {
            AppColor.primary.color.edgesIgnoringSafeArea(.all)
            Image("medico_logo")
                .resizable()
                .scaledToFit()
                .frame(width: 150)
        }
    }
    
    init() {
        currentScope = SwiftDatasource(dataSource: navigator.scope)
        
        setUpNavigationBar()
    }
    
    private func setUpNavigationBar() {
        let appearance = UINavigationBarAppearance()
        appearance.backgroundColor = UIColor(hex: 0xD9EDF9)
        appearance.shadowColor = .clear
        
        var titleTextAttributes: [NSAttributedString.Key: Any] = [.foregroundColor: UIColor(hex: 0x003657)]
        if let titleFont = UIFont(name: "Barlow-SemiBold", size: 17) {
            titleTextAttributes[.font] = titleFont
        }
        appearance.titleTextAttributes = titleTextAttributes
        
        UINavigationBar.appearance().standardAppearance = appearance
        UINavigationBar.appearance().scrollEdgeAppearance = appearance
    }
}

struct VisualEffectView: UIViewRepresentable {
    var effect: UIVisualEffect?
    func makeUIView(context: UIViewRepresentableContext<Self>) -> UIVisualEffectView { UIVisualEffectView() }
    func updateUIView(_ uiView: UIVisualEffectView, context: UIViewRepresentableContext<Self>) {
        uiView.effect = effect
    }
}
