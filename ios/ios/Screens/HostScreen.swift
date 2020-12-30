import SwiftUI
import core

struct HostScreen: View {
    @State private var isSplashScreenActive = true
    
    @ObservedObject var currentScope: SwiftDataSource<BaseScope>
    
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
            if let scope = currentScope.value {
                BaseScopeView(scope: scope)
            }
        }
    }
    
    var splashScreen: some View {
        ZStack {
            AppColor.primary.color.edgesIgnoringSafeArea(.all)
            Image("medico_logo")
        }
    }
    
    init() {
        currentScope = SwiftDataSource(dataSource: UIApplication.shared.navigator.scope)
        
        setUpNavigationBar()
    }
    
    private func setUpNavigationBar() {
        let appearance = UINavigationBarAppearance()
        appearance.backgroundColor = UIColor(named: "NavigationBar")
        
        var titleTextAttributes: [NSAttributedString.Key: Any] = [.foregroundColor: UIColor(hex: 0x003657)]
        if let titleFont = UIFont(name: TextWeight.semiBold.fontName, size: 17) {
            titleTextAttributes[.font] = titleFont
        }
        appearance.titleTextAttributes = titleTextAttributes
        
        UINavigationBar.appearance().standardAppearance = appearance
        UINavigationBar.appearance().scrollEdgeAppearance = appearance
    }
}

struct BaseScopeView: View {
    let scope: BaseScope
    @ObservedObject var isInProgress: SwiftDataSource<KotlinBoolean>
    
    var body: some View {
        ZStack {
            NavigationView {
                ZStack {
                    AppColor.primary.color.edgesIgnoringSafeArea(.all)
                
                    getCurrentViewWithModifiers()
                }
                .hideKeyboardOnTap()
            }

            if let isInProgress = self.isInProgress.value,
               isInProgress == true {
                ActivityView()
            }
        }
    }
    
    var currentView: AnyView {
        switch scope {
            
        case let scopeValue as LogInScope:
            return AnyView(AuthScreen(scope: scopeValue))
            
        case let scopeValue as MainScope:
            return AnyView(MainScreen(scope: scopeValue))
            
        case let scopeValue as OtpScope:
            return AnyView(OtpFlowScreen(scope: scopeValue))
            
        case let scopeValue as EnterNewPasswordScope:
            return AnyView(AuthNewPasswordScreen(scope: scopeValue))
            
        case let scopeValue as SignUpScope:
            return AnyView(SignUpScreen(scope: scopeValue))
            
        default:
            return AnyView(EmptyView())
        }
    }
    
    private func getCurrentViewWithModifiers() -> some View {
        var view = self.currentView
        
        if let scopeWithErrors = scope as? WithErrors {
            view = AnyView(view.errorAlert(withHandler: scopeWithErrors))
        }
        
        if let goBackScope = scope as? CanGoBack {
            view = AnyView(view.backButton { goBackScope.goBack() })
        }
        
        return view
    }
    
    init(scope: BaseScope) {
        self.scope = scope
        
        self.isInProgress = SwiftDataSource(dataSource: scope.isInProgress)
    }
}
