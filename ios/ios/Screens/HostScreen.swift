import SwiftUI
import core

struct HostScreen: View {
    @State private var isSplashScreenActive = true
    
    @ObservedObject var currentScope: SwiftDataSource<Scope.Host>
    
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
                SettingsScreen()
//                BaseScopeView(scope: scope)
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
    }
}

struct BaseScopeView: View {
    let scope: Scope.Host
    
    @ObservedObject var isInProgress: SwiftDataSource<KotlinBoolean>
    
    var body: some View {
        ZStack {
            NavigationView {
                ZStack {
                    AppColor.primary.color.edgesIgnoringSafeArea(.all)
                
                    getViewWithModifiers()
                }
                .hideKeyboardOnTap()
                .navigationBarHidden(true)
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
            
        case let scopeValue as WelcomeScope:
            return AnyView(WelcomeScreen(welcomeOption: WelcomeOption.Thanks { scopeValue.accept() },
                                         userName: scopeValue.fullName))
            
        case let scopeValue as SearchScope:
            return AnyView(GlobalSearchScreen(scope: scopeValue))
            
        case let scopeValue as Scope.Host.HostTabBar:
            return AnyView(TabBarScreen(tabBarScope: scopeValue))
            
        default:
            return AnyView(EmptyView())
        }
    }
    
    init(scope: Scope.Host) {
        self.scope = scope
        
        self.isInProgress = SwiftDataSource(dataSource: scope.isInProgress)
    }
    
    private func getViewWithModifiers() -> some View {
        var view = AnyView(
            currentView
                .errorAlert(withHandler: scope)
        )
        
        if let uploadBottomSheet = scope.bottomSheet as? DataSource<BottomSheet.UploadDocuments> {
            view = AnyView(
                view
                    .filePicker(bottomSheet: uploadBottomSheet,
                                onBottomSheetDismiss: { scope.dismissBottomSheet() })
            )
        }
        
        return view
    }
}

struct TabBarScreen: View {
    let tabBarScope: Scope.Host.HostTabBar
    
    @ObservedObject var scope: SwiftDataSource<Scope.ChildTabBar>
    
    var body: some View {
        currentView
            .navigationBar(withNavigationSection: tabBarScope.navigationSection,
                           withNavigationBarInfo: tabBarScope.tabBar,
                           handleGoBack: { tabBarScope.goBack() })
    }
    
    init(tabBarScope: Scope.Host.HostTabBar) {
        self.tabBarScope = tabBarScope
        
        self.scope = SwiftDataSource(dataSource: tabBarScope.childScope)
    }
    
    private var currentView: AnyView {
        switch scope.value {
            
        case let scopeValue as OtpScope:
            return AnyView(OtpFlowScreen(scope: scopeValue))
            
        case let scopeValue as EnterNewPasswordScope:
            return AnyView(AuthNewPasswordScreen(scope: scopeValue))
            
        case let scopeValue as SignUpScope:
            return AnyView(SignUpScreen(scope: scopeValue))
            
        case let scope as LimitedAccessScope:
            return AnyView(LimitedAppAccessScreen(scope: scope))
            
        case let scope as ProductInfoScope:
            return AnyView(ProductDetails(scope: scope))
            
        default:
            return AnyView(EmptyView())
        }
    }
}
