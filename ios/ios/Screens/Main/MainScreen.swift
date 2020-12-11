import SwiftUI
import core

struct MainScreen: View {
    let scope: MainScope
    
    var body: some View {
        getCurrentView()
            .userInfoNavigationBar(isLimitedAppAccess: scope is MainScope.LimitedAccess) {
                scope.tryLogOut()
            }
    }
    
    private func getCurrentView() -> some View {
        let view: AnyView
        
        switch self.scope {
        
        case let scope as MainScope.LimitedAccess:
            view = AnyView(LimitedAppAccessScreen(scope: scope))
            
        default:
            view = AnyView(EmptyView())
        }
        
        return view
    }
}
